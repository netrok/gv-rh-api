// src/main/java/com/gv/mx/core/auth/AuthController.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.dto.AuthDtos.LoginRequest;
import com.gv.mx.core.auth.dto.AuthDtos.LogoutRequest;
import com.gv.mx.core.auth.dto.AuthDtos.RefreshRequest;
import com.gv.mx.core.auth.dto.AuthDtos.TokenPairResponse;
import com.gv.mx.core.auth.dto.SignUpDtos.CreateUserRequest;
import com.gv.mx.core.auth.dto.SignUpDtos.UserResponse;
import com.gv.mx.core.auth.dto.UsersDtos.AdminChangePasswordRequest;
import com.gv.mx.core.auth.dto.UsersDtos.ChangePasswordRequest;
import com.gv.mx.core.auth.dto.UsersDtos.RolesUpdateRequest;
import com.gv.mx.core.auth.dto.UsersDtos.UserView;
import com.gv.mx.core.auth.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final JwtService jwt;
    private final UserAdminService userAdmin;
    private final RefreshTokenService refreshSvc;
    private final UserRepository usersRepo;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtService jwt,
                          UserAdminService userAdmin,
                          RefreshTokenService refreshSvc,
                          UserRepository usersRepo) {
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
        this.userAdmin = userAdmin;
        this.refreshSvc = refreshSvc;
        this.usersRepo = usersRepo;
    }

    private String clientIp() {
        var req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip.split(",")[0].trim() : req.getRemoteAddr();
    }

    private String clientUA() {
        var req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return Optional.ofNullable(req.getHeader("User-Agent")).orElse("unknown");
    }

    // ---- Login (emite access con roles/scope + refresh rotativo persistido)
    @PostMapping(value = "/login", consumes = "application/json")
    @Operation(summary = "Obtener access/refresh tokens (con rotación)")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );

        // Firmar access con authorities reales (roles/scope)
        UserDetails user = uds.loadUserByUsername(auth.getName());
        String access = jwt.generateAccess(user);

        // Buscar userId (sin IgnoreCase)
        Long userId = usersRepo.findByUsername(user.getUsername())
                .map(UserAccount::getId)
                .orElseThrow(() -> new BadCredentialsException("Usuario no existe"));

        // Crear familia y jti inicial, persistir refresh emitido
        UUID family = refreshSvc.newFamilyId();
        UUID jti = refreshSvc.newJti();
        refreshSvc.issue(userId, family, jti, clientIp(), clientUA());

        String refresh = jwt.generateRefresh(user, family, jti);
        return ResponseEntity.ok(new TokenPairResponse(access, refresh));
    }

    // ---- Refresh (rotación + detección de reuso)
    @PostMapping(value = "/refresh", consumes = "application/json")
    @Operation(summary = "Obtener nuevo access/refresh (rotación)")
    public ResponseEntity<TokenPairResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        Jwt decoded = jwt.decode(req.refresh);
        if (!"refresh".equals(decoded.getClaimAsString("typ"))) {
            throw new BadCredentialsException("Refresh token inválido (typ != refresh)");
        }
        UUID jti = UUID.fromString(decoded.getClaimAsString("jti"));
        UUID familyId = UUID.fromString(decoded.getClaimAsString("family_id"));
        String username = decoded.getSubject();

        var newRt = refreshSvc.rotateOrDetectReuse(jti);

        UserDetails user = uds.loadUserByUsername(username);
        String access = jwt.generateAccess(user);
        String newRefresh = jwt.generateRefresh(user, familyId, newRt.getJti());

        return ResponseEntity.ok(new TokenPairResponse(access, newRefresh));
    }

    // ---- Logout de la sesión (familia actual). Requiere access token + refresh body.
    @PostMapping(value = "/logout", consumes = "application/json")
    @Operation(summary = "Logout de la sesión (familia actual). Requiere refresh.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest body) {
        Jwt decoded = jwt.decode(body.refresh);
        if (!"refresh".equals(decoded.getClaimAsString("typ"))) {
            throw new BadCredentialsException("Refresh inválido");
        }
        UUID familyId = UUID.fromString(decoded.getClaimAsString("family_id"));
        refreshSvc.revokeFamily(familyId, "logout");
        return ResponseEntity.noContent().build();
    }

    // ---- Logout de todas las sesiones del usuario autenticado
    @PostMapping(value = "/logout-all")
    @Operation(summary = "Logout de todas las sesiones del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAll(Principal principal) {
        Long userId = usersRepo.findByUsername(principal.getName())
                .map(UserAccount::getId)
                .orElseThrow(() -> new BadCredentialsException("Usuario no existe"));
        refreshSvc.revokeAllByUser(userId, "logout_all");
        return ResponseEntity.noContent().build();
    }

    // ---- Signup (ADMIN-only)
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody CreateUserRequest req) {
        UserResponse created = userAdmin.createUser(req);
        return ResponseEntity.status(201).body(created);
    }

    // ---- Listar usuarios (ADMIN-only)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserView>> listUsers() {
        return ResponseEntity.ok(userAdmin.listUsers());
    }

    // ---- Detalle de usuario por id (ADMIN-only)
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Detalle de usuario por id (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserView> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userAdmin.getUser(id));
    }

    // ---- Habilitar usuario (ADMIN-only)
    @PutMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Habilitar usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserView> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userAdmin.enableUser(id));
    }

    // ---- Deshabilitar usuario (ADMIN-only)
    @PutMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deshabilitar usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserView> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userAdmin.disableUser(id));
    }

    // ---- Agregar roles (ADMIN-only)
    @PostMapping(value = "/users/{id}/roles", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar roles a un usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserView> addRoles(@PathVariable Long id,
                                             @Valid @RequestBody RolesUpdateRequest req) {
        return ResponseEntity.ok(userAdmin.addRoles(id, req));
    }

    // ---- Quitar rol (ADMIN-only)
    @DeleteMapping("/users/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar un rol a un usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserView> removeRole(@PathVariable Long id, @PathVariable String role) {
        return ResponseEntity.ok(userAdmin.removeRole(id, role));
    }

    // ---- Cambiar mi contraseña (autenticado)
    @PostMapping(value = "/change-password", consumes = "application/json")
    @Operation(summary = "Cambiar mi contraseña (autenticado)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(Principal principal,
                                               @Valid @RequestBody ChangePasswordRequest req) {
        userAdmin.changeOwnPassword(principal.getName(), req.currentPassword, req.newPassword);
        return ResponseEntity.noContent().build();
    }

    // ---- Cambiar contraseña de un usuario (ADMIN-only)
    @PostMapping(value = "/admin/change-password/{username}", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar contraseña de un usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> adminChangePassword(@PathVariable String username,
                                                    @Valid @RequestBody AdminChangePasswordRequest req) {
        userAdmin.adminChangePassword(username, req.newPassword);
        return ResponseEntity.noContent().build();
    }
}

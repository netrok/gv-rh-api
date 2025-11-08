// src/main/java/com/gv/mx/core/auth/AuthController.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.dto.AuthDtos.AccessResponse;
import com.gv.mx.core.auth.dto.AuthDtos.LoginRequest;
import com.gv.mx.core.auth.dto.AuthDtos.RefreshRequest;
import com.gv.mx.core.auth.dto.AuthDtos.TokenPairResponse;
import com.gv.mx.core.auth.dto.SignUpDtos.CreateUserRequest;
import com.gv.mx.core.auth.dto.SignUpDtos.UserResponse;
import com.gv.mx.core.auth.dto.UsersDtos.AdminChangePasswordRequest;
import com.gv.mx.core.auth.dto.UsersDtos.ChangePasswordRequest;
import com.gv.mx.core.auth.dto.UsersDtos.UserView;
import com.gv.mx.core.auth.dto.UsersDtos.RolesUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final JwtService jwt;
    private final UserAdminService userAdmin;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtService jwt,
                          UserAdminService userAdmin) {
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
        this.userAdmin = userAdmin;
    }

    // ---- Login
    @PostMapping(value = "/login", consumes = "application/json")
    @Operation(summary = "Obtener access/refresh tokens")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );
        String access = jwt.generateAccess(auth);
        UserDetails user = uds.loadUserByUsername(auth.getName());
        String refresh = jwt.generateRefresh(user);
        return ResponseEntity.ok(new TokenPairResponse(access, refresh));
    }

    // ---- Refresh
    @PostMapping(value = "/refresh", consumes = "application/json")
    @Operation(summary = "Obtener nuevo access token usando refresh")
    public ResponseEntity<AccessResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        Jwt decoded = jwt.decode(req.refresh);
        String typ = decoded.getClaimAsString("typ");
        if (!"refresh".equals(typ)) {
            throw new BadCredentialsException("Refresh token inválido (typ != refresh)");
        }
        String username = decoded.getSubject();
        UserDetails user = uds.loadUserByUsername(username);
        String newAccess = jwt.generateAccess(user);
        return ResponseEntity.ok(new AccessResponse(newAccess));
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

// src/main/java/com/gv/mx/core/auth/AuthController.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.dto.AuthDtos.AccessResponse;
import com.gv.mx.core.auth.dto.AuthDtos.LoginRequest;
import com.gv.mx.core.auth.dto.AuthDtos.RefreshRequest;
import com.gv.mx.core.auth.dto.AuthDtos.TokenPairResponse;
import com.gv.mx.core.auth.dto.SignUpDtos.CreateUserRequest;
import com.gv.mx.core.auth.dto.SignUpDtos.UserResponse;
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

    @PostMapping(value = "/refresh", consumes = "application/json")
    @Operation(summary = "Obtener nuevo access token usando refresh")
    @SecurityRequirement(name = "bearerAuth")
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

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario (ADMIN-only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody CreateUserRequest req) {
        UserResponse created = userAdmin.createUser(req);
        return ResponseEntity.status(201).body(created);
    }
}

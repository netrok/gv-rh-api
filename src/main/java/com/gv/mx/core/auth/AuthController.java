package com.gv.mx.core.auth;

import com.gv.mx.core.auth.dto.AuthDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final JwtService jwt;

    public AuthController(AuthenticationManager authManager, UserDetailsService uds, JwtService jwt) {
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    @Operation(summary = "Obtener access/refresh tokens")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );
        // genera tokens
        String access = jwt.generateAccess(auth);
        UserDetails user = uds.loadUserByUsername(auth.getName());
        String refresh = jwt.generateRefresh(user);
        return ResponseEntity.ok(new TokenPairResponse(access, refresh));
    }

    @PostMapping(value = "/refresh", consumes = "application/json")
    @Operation(summary = "Obtener nuevo access token usando refresh")
    public ResponseEntity<AccessResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        Jwt decoded = jwt.decode(req.refresh);

        // valida tipo "refresh"
        String typ = decoded.getClaimAsString("typ");
        if (!"refresh".equals(typ)) {
            throw new BadCredentialsException("Refresh token inválido (typ != refresh)");
        }

        String username = decoded.getSubject();
        UserDetails user = uds.loadUserByUsername(username);

        String newAccess = jwt.generateAccess(user);
        // (opcional) rotar refresh si quieres:
        // String newRefresh = jwt.generateRefresh(user);
        return ResponseEntity.ok(new AccessResponse(newAccess));
    }
}

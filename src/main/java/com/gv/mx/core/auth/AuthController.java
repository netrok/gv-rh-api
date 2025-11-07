package com.gv.mx.core.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    private final JwtService jwt;

    public AuthController(JwtService jwt) {
        this.jwt = jwt;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Login y emisión de JWT (demo)")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        // DEMO: credenciales fijas
        if ("admin".equals(body.username()) && "Admin123*".equals(body.password())) {
            // Usa tu implementación existente JwtService.issue(subject, roles...)
            String token = jwt.issue(body.username(), "ADMIN", "RRHH");
            return ResponseEntity.ok(Map.of(
                    "token_type", "Bearer",
                    "access_token", token
            ));
        }
        return ResponseEntity.status(401).build();
    }
}

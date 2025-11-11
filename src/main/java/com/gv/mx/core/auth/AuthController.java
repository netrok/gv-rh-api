package com.gv.mx.core.auth;

import com.gv.mx.core.auth.dto.AuthDtos.AccessResponse;
import com.gv.mx.core.auth.dto.AuthDtos.LoginRequest;
import com.gv.mx.core.auth.dto.AuthDtos.RefreshRequest;
import com.gv.mx.core.auth.dto.AuthDtos.TokenPairResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final JwtService jwt;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtService jwt) {
        this.authManager = authManager;
        this.uds = uds;
        this.jwt = jwt;
    }

    // ==== Login -> { access, refresh } ====
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );
        String access = jwt.generateAccess(auth);

        // Refresh simple (sin rotación/BD). Generamos family/jti ad-hoc.
        UserDetails user = (UserDetails) auth.getPrincipal();
        UUID familyId = UUID.randomUUID();
        UUID jti = UUID.randomUUID();
        String refresh = jwt.generateRefresh(user, familyId, jti);

        return ResponseEntity.ok(new TokenPairResponse(access, refresh));
    }

    // ==== Refresh "plano" -> { access } ====
    // Nota: aquí NO rotamos ni validamos en BD. Úsalo sólo en dev hasta activar la rotación.
    @PostMapping(value = "/refresh", consumes = "application/json")
    public ResponseEntity<AccessResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        Jwt parsed = jwt.decode(req.refresh);
        String typ = parsed.getClaimAsString("typ");
        if (typ == null || !"refresh".equalsIgnoreCase(typ)) {
            return ResponseEntity.badRequest().build();
        }
        String username = parsed.getSubject();
        UserDetails user = uds.loadUserByUsername(username);
        String access = jwt.generateAccess(user);
        return ResponseEntity.ok(new AccessResponse(access));
    }

    // ==== WhoAmI ====
    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(Principal principal) {
        return ResponseEntity.ok(principal == null ? "anonymous" : principal.getName());
    }
}

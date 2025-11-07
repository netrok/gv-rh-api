package com.gv.mx.core.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access.minutes:60}")
    private long accessMinutes;

    public String issueAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant exp  = now.plusSeconds(accessMinutes * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                .subject(username)
                .claim("roles", roles)                      // ["ADMIN","RRHH"]
                .claim("scope", String.join(" ", roles))    // opcional
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

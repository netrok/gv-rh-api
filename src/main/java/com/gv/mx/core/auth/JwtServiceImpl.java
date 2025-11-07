// src/main/java/com/gv/mx/core/auth/JwtServiceImpl.java
package com.gv.mx.core.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final String issuer;
    private final long accessMinutes;

    public JwtServiceImpl(
            JwtEncoder encoder,
            JwtDecoder decoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-minutes}") long accessMinutes
    ) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
    }

    @Override
    public String issue(String username, String... roles) {
        Instant now = Instant.now();

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("scope", String.join(" ", roles));
        claimsMap.put("roles", Arrays.asList(roles));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(accessMinutes, ChronoUnit.MINUTES))
                .subject(username)
                .claims(c -> c.putAll(claimsMap))
                .build();

        // 👇 MUY IMPORTANTE: Header HS256 para que ImmutableSecret sea seleccionable
        JwsHeader jws = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(jws, claims)).getTokenValue();
    }

    @Override
    public List<String> getRoles(String token) {
        Jwt jwt = decoder.decode(token);
        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim instanceof Collection<?> col) {
            List<String> out = new ArrayList<>();
            for (Object o : col) out.add(String.valueOf(o));
            return out;
        }
        String scope = jwt.getClaimAsString("scope");
        if (scope != null && !scope.isBlank()) {
            return Arrays.asList(scope.split("\\s+"));
        }
        return Collections.emptyList();
    }
}

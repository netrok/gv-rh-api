package com.gv.mx.core.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder; // para getRoles()
    private final String issuer;
    private final long accessMinutes;
    private final long refreshMinutes;

    public JwtServiceImpl(
            JwtEncoder encoder,
            JwtDecoder decoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-minutes}") long accessMinutes,
            @Value("${app.jwt.refresh-minutes}") long refreshMinutes
    ) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.refreshMinutes = refreshMinutes;
    }

    @Override
    public String issueAccess(String subject, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(accessMinutes, ChronoUnit.MINUTES))
                .subject(subject)
                .claim("typ", "access")
                .claim("roles", roles)
                // opcional: también en scope para compatibilidad con convertidores
                .claim("scope", String.join(" ", roles))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Override
    public String issueRefresh(String subject) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(refreshMinutes, ChronoUnit.MINUTES))
                .subject(subject)
                .claim("typ", "refresh")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // Compatibilidad con llamadas antiguas
    @Override
    public String issue(String subject, String... roles) {
        return issueAccess(subject, Arrays.asList(roles));
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

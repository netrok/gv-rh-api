// src/main/java/com/gv/mx/core/auth/JwtServiceImpl.java
package com.gv.mx.core.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final long refreshMinutes;

    public JwtServiceImpl(JwtEncoder encoder,
                          JwtDecoder decoder,
                          @Value("${app.jwt.issuer}") String issuer,
                          @Value("${app.jwt.access-minutes}") long accessMinutes,
                          @Value("${app.jwt.refresh-minutes}") long refreshMinutes) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.refreshMinutes = refreshMinutes;
    }

    @Override
    public String generateAccess(Authentication auth) {
        var roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .distinct()
                .toList();
        return generateAccessInternal(auth.getName(), roles);
    }

    @Override
    public String generateAccess(UserDetails user) {
        var roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .distinct()
                .toList();
        return generateAccessInternal(user.getUsername(), roles);
    }

    private String generateAccessInternal(String username, List<String> roles) {
        Map<String, Object> claims = baseClaims("access", roles);
        return encodeWithExp(username, claims, accessMinutes);
    }

    @Override
    public String generateRefresh(UserDetails user, UUID familyId, UUID jti) {
        var roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .distinct()
                .toList();

        Map<String, Object> claims = baseClaims("refresh", roles);
        // Null-safety
        if (familyId != null) claims.put("family_id", familyId.toString());
        if (jti != null)      claims.put("jti", jti.toString());

        // Asignamos exp para refresh; la rotación/blacklist la controla BD
        return encodeWithExp(user.getUsername(), claims, refreshMinutes);
    }

    @Override
    public Jwt decode(String token) {
        return decoder.decode(token);
    }

    // ===== Helpers =====

    private Map<String, Object> baseClaims(String typ, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        var cleaned = roles == null ? List.<String>of() : roles;
        claims.put("roles", cleaned);
        claims.put("scope", String.join(" ", cleaned));
        claims.put("typ", typ);
        return claims;
    }

    private String encodeWithExp(String subject, Map<String, Object> claims, long minutes) {
        Instant now = Instant.now();
        JwtClaimsSet set = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(minutes, ChronoUnit.MINUTES))
                .claims(c -> c.putAll(claims))
                .build();

        JwsHeader jws = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(jws, set)).getTokenValue();
    }
}

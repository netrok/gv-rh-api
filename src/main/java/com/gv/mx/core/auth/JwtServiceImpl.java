// src/main/java/com/gv/mx/core/auth/JwtServiceImpl.java
package com.gv.mx.core.auth;

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

    public JwtServiceImpl(JwtEncoder encoder, JwtDecoder decoder, JwtProperties props) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.issuer = props.getIssuer();
        this.accessMinutes = Math.max(1, props.getAccessMinutes());
        this.refreshMinutes = Math.max(1, props.getRefreshMinutes());
    }

    @Override
    public String generateAccess(Authentication auth) {
        var roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", "")) // ROLE_ADMIN -> ADMIN
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

        // Si tiene ADMIN o RRHH, otorgamos scope para reportes
        if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN") || r.equalsIgnoreCase("RRHH"))) {
            claims.put("scope", joinScopes(claims.get("scope"), "reportes.read"));
        }

        return encodeWithExp(username, claims, accessMinutes);
    }

    @Override
    public String generateRefresh(UserDetails user, UUID familyId, UUID jti) {
        var roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .distinct()
                .toList();

        Map<String, Object> claims = baseClaims("refresh", roles);
        if (familyId != null) claims.put("family_id", familyId.toString());
        if (jti != null)      claims.put("jti", jti.toString());

        return encodeWithExp(user.getUsername(), claims, refreshMinutes);
    }

    @Override
    public Jwt decode(String token) {
        return decoder.decode(token);
    }

    // -------- helpers --------
    private Map<String, Object> baseClaims(String typ, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles == null ? List.<String>of() : roles);
        claims.put("typ", typ);
        return claims;
    }

    /** Une scopes existentes con nuevos, en formato espacio-separado (RFC 8693-like). */
    private String joinScopes(Object existingScope, String... toAdd) {
        var set = new LinkedHashSet<String>();
        if (existingScope instanceof String s && !s.isBlank()) {
            set.addAll(Arrays.asList(s.trim().split("\\s+")));
        }
        for (var s : toAdd) if (s != null && !s.isBlank()) set.add(s);
        return String.join(" ", set);
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

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
        this.accessMinutes = Math.max(1, props.getAccessMinutes());   // hardening mínimo 1 min
        this.refreshMinutes = Math.max(1, props.getRefreshMinutes()); // hardening mínimo 1 min
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
        if (familyId != null) claims.put("family_id", familyId.toString()); // <-- usado por AuthController
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
        var cleaned = (roles == null) ? List.<String>of() : roles;
        claims.put("roles", cleaned);
        claims.put("scope", String.join(" ", cleaned));
        claims.put("typ", typ);
        return claims;
    }

    private String encodeWithExp(String subject, Map<String, Object> claims, long minutes) {
        // Un solo reloj para evitar “expiresAt must be after issuedAt”
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

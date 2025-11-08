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

    public JwtServiceImpl(JwtEncoder encoder,
                          JwtDecoder decoder,
                          @Value("${app.jwt.issuer}") String issuer,
                          @Value("${app.jwt.access-minutes}") long accessMinutes) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
    }

    @Override
    public String generateAccess(Authentication auth) {
        var roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        return generateAccessInternal(auth.getName(), roles);
    }

    @Override
    public String generateAccess(UserDetails user) {
        var roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        return generateAccessInternal(user.getUsername(), roles);
    }

    private String generateAccessInternal(String username, List<String> roles) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("scope", String.join(" ", roles));
        claims.put("typ", "access");

        JwtClaimsSet set = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plus(accessMinutes, ChronoUnit.MINUTES))
                .claims(c -> c.putAll(claims))
                .build();

        JwsHeader jws = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(jws, set)).getTokenValue();
    }

    @Override
    public String generateRefresh(UserDetails user, UUID familyId, UUID jti) {
        Instant now = Instant.now();
        var roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("scope", String.join(" ", roles));
        claims.put("typ", "refresh");
        claims.put("family_id", familyId.toString());
        claims.put("jti", jti.toString());

        // La expiración real del refresh la controla la BD (registro en auth_refresh_tokens)
        JwtClaimsSet set = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getUsername())
                .issuedAt(now)
                .claims(c -> c.putAll(claims))
                .build();

        JwsHeader jws = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(jws, set)).getTokenValue();
    }

    @Override
    public Jwt decode(String token) {
        return decoder.decode(token);
    }
}

package com.gv.mx.core.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final JwtProperties props;

    public JwtService(JwtEncoder encoder, JwtDecoder decoder, JwtProperties props) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.props = props;
    }

    public String generateAccess(UserDetails user) {
        return encode(user, props.getAccessMinutes(), "access");
    }

    public String generateAccess(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) return generateAccess(ud);
        // fallback mínimo
        var ud = org.springframework.security.core.userdetails.User
                .withUsername(auth.getName())
                .password("N/A")
                .authorities(auth.getAuthorities())
                .build();
        return generateAccess(ud);
    }

    public String generateRefresh(UserDetails user) {
        return encode(user, props.getRefreshMinutes(), "refresh");
    }

    private String encode(UserDetails user, int minutes, String typ) {
        Instant now = Instant.now();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plus(minutes, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("roles", roles)
                .claim("typ", typ)
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader jws = JwsHeader.with(() -> "HS256").build();
        return this.encoder.encode(JwtEncoderParameters.from(jws, claims)).getTokenValue();
    }

    public Jwt decode(String token) {
        return decoder.decode(token);
    }
}

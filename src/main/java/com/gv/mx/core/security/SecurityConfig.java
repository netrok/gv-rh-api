package com.gv.mx.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String secretBase64; // debe ser Base64

    @Value("${app.jwt.issuer}")
    private String issuer;

    // ===== JWT Encoder/Decoder (HS256 con secret base64) =====
    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        NimbusJwtDecoder dec = NimbusJwtDecoder.withSecretKey(secretKey).build();
        dec.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return dec;
    }

    // ===== Convierte claims -> authorities =====
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        var conv = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwt -> {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) jwt.getClaims().get("roles"); // ["ADMIN","RRHH"]

            if (roles == null) {
                String scope = jwt.getClaimAsString("scope"); // "ADMIN RRHH"
                roles = (scope == null || scope.isBlank()) ? List.of() : Arrays.asList(scope.split("\\s+"));
            }

            // normaliza a ROLE_*
            return roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        });
        return conv;
    }

    // ===== Seguridad HTTP =====
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthConv
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // público
                        .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/health").permitAll()
                        // export empleados: solo ADMIN o RRHH
                        .requestMatchers(HttpMethod.GET, "/api/empleados/export").hasAnyRole("ADMIN","RRHH")
                        // demás rutas autenticadas
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConv)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}

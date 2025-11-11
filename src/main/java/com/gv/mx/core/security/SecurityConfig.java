// src/main/java/com/gv/mx/core/security/SecurityConfig.java
package com.gv.mx.core.security;

import com.gv.mx.core.auth.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private final JwtProperties props;
    public SecurityConfig(JwtProperties props) { this.props = props; }

    @PostConstruct
    void checkProps() {
        if (props.getSecret() == null || props.getSecret().isBlank())
            throw new IllegalStateException("app.jwt.secret (BASE64) no configurado");
        if (props.getIssuer() == null || props.getIssuer().isBlank())
            throw new IllegalStateException("app.jwt.issuer no configurado");
    }

    // ===== Básicos =====
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // ===== JWT (Nimbus) =====
    private SecretKey buildSecretKey() {
        byte[] key = Base64.getDecoder().decode(props.getSecret());
        return new SecretKeySpec(key, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(buildSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder dec = NimbusJwtDecoder.withSecretKey(buildSecretKey()).build();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(props.getIssuer());
        JwtTimestampValidator ts = new JwtTimestampValidator(Duration.ofSeconds(30)); // clock skew
        dec.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, ts));
        return dec;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
        gac.setAuthoritiesClaimName("roles"); // tu claim
        gac.setAuthorityPrefix("ROLE_");      // prefijo estándar
        JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(gac);
        return jac;
    }

    // ===== CORS =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        cfg.addExposedHeader("Content-Disposition");
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    // ===== Security Filter Chain =====
    @Bean
    public SecurityFilterChain security(HttpSecurity http, JwtAuthenticationConverter jwtConv) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/v3/api-docs.yaml", "/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Reportes → requieren rol
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN","RRHH")

                        // Ejemplo sensible
                        .requestMatchers(HttpMethod.GET, "/api/empleados/export").hasAnyRole("ADMIN","RRHH")

                        // Resto autenticado
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(jwtConv)));

        return http.build();
    }
}

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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

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

    // ===== Users de demo (cámbialos a BD cuando quieras) =====
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("Admin123*"))
                .roles("ADMIN","RRHH")
                .build();
        UserDetails rrhh = User.withUsername("rrhh")
                .password(encoder.encode("Rrhh123*"))
                .roles("RRHH")
                .build();
        return new InMemoryUserDetailsManager(admin, rrhh);
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // 👉 Sin DaoAuthenticationProvider manual (evitamos deprecations)
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

        // Validador por issuer + timestamps
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(props.getIssuer());

        // ✅ Clock skew con constructor (no setClockSkew)
        JwtTimestampValidator ts = new JwtTimestampValidator(Duration.ofSeconds(30));

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, ts);
        dec.setJwtValidator(validator);
        return dec;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        var c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(jwt -> {
            var roles = jwt.getClaimAsStringList("roles");
            if (roles == null) roles = List.of();
            return roles.stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                    .map(org.springframework.security.core.GrantedAuthority.class::cast)
                    .toList();
        });
        return c;
    }

    // ===== CORS =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        cfg.addAllowedOriginPattern("*");
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        cfg.addExposedHeader("Content-Disposition");
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    // ===== Cadena única =====
    @Bean
    public SecurityFilterChain security(HttpSecurity http, JwtAuthenticationConverter jwtConv) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs/swagger-config",
                                "/", "/error", "/favicon.ico"
                        ).permitAll()
                        // Salud y Auth
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Ejemplo sensible
                        .requestMatchers(HttpMethod.GET, "/api/empleados/export").hasAnyRole("ADMIN","RRHH")
                        // Resto autenticado
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtConv)));
        return http.build();
    }
}

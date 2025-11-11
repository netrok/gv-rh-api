// src/main/java/com/gv/mx/core/auth/JwtProperties.java
package com.gv.mx.core.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /** Clave Base64 para HS256 */
    private String secret;
    /** Issuer del token */
    private String issuer;
    /** Minutos de vida del access token (yml: access-minutes) */
    private long accessMinutes;
    /** Minutos de vida del refresh token (yml: refresh-minutes) */
    private long refreshMinutes;
    /** Límite de familias de refresh activas por usuario (yml: max-families-per-user, default 5) */
    private int maxFamiliesPerUser = 5;
}

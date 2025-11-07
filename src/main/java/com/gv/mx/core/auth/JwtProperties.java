package com.gv.mx.core.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;        // BASE64
    private String issuer;
    private int accessMinutes;
    private int refreshMinutes;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public int getAccessMinutes() { return accessMinutes; }
    public void setAccessMinutes(int accessMinutes) { this.accessMinutes = accessMinutes; }

    public int getRefreshMinutes() { return refreshMinutes; }
    public void setRefreshMinutes(int refreshMinutes) { this.refreshMinutes = refreshMinutes; }
}

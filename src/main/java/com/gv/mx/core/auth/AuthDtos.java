// src/main/java/com/gv/mx/core/auth/dto/AuthDtos.java
package com.gv.mx.core.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank public String username;
        @NotBlank public String password;
    }

    public static class TokenPairResponse {
        public String access;
        public String refresh;
        public TokenPairResponse(String access, String refresh) {
            this.access = access;
            this.refresh = refresh;
        }
    }

    public static class RefreshRequest {
        @NotBlank public String refresh;
    }

    public static class AccessResponse {
        public String access;
        public AccessResponse(String access) {
            this.access = access;
        }
    }

    public static class LogoutRequest {
        @NotBlank public String refresh;
    }
}

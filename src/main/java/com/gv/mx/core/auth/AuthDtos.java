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
        public TokenPairResponse(String a, String r) { this.access = a; this.refresh = r; }
    }

    public static class RefreshRequest {
        @NotBlank public String refresh;
    }

    public static class AccessResponse {
        public String access;
        public AccessResponse(String a) { this.access = a; }
    }
}

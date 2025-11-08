// src/main/java/com/gv/mx/core/auth/dto/UsersDtos.java
package com.gv.mx.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UsersDtos {

    public static class UserView {
        public Long id;
        public String username;
        public boolean enabled;
        public List<String> roles;

        public UserView(Long id, String username, boolean enabled, List<String> roles) {
            this.id = id;
            this.username = username;
            this.enabled = enabled;
            this.roles = roles;
        }
    }

    public static class ChangePasswordRequest {
        @NotBlank public String currentPassword;

        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
                message = "Password debe incluir mayúscula, minúscula, dígito y caracter especial"
        )
        public String newPassword;
    }

    public static class AdminChangePasswordRequest {
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
                message = "Password debe incluir mayúscula, minúscula, dígito y caracter especial"
        )
        public String newPassword;
    }

    public static class RolesUpdateRequest {
        public List<@NotBlank String> roles;
    }
}

package com.gv.mx.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public class SignUpDtos {

    public static class CreateUserRequest {
        @NotBlank @Size(min = 3, max = 80)
        public String username;

        // Mínimo ejemplo de política: 8+, al menos 1 mayúscula, 1 minúscula, 1 dígito, 1 especial
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
                message = "Password debe incluir mayúscula, minúscula, dígito y caracter especial")
        public String password;

        // Ej: ["ADMIN","RRHH"]
        public List<@NotBlank String> roles;
    }

    public static class UserResponse {
        public Long id;
        public String username;
        public boolean enabled;
        public List<String> roles;

        public UserResponse(Long id, String username, boolean enabled, List<String> roles) {
            this.id = id;
            this.username = username;
            this.enabled = enabled;
            this.roles = roles;
        }
    }
}

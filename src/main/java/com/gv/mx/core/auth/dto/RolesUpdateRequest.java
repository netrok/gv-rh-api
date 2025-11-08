// src/main/java/com/gv/mx/core/auth/dto/RolesUpdateRequest.java
package com.gv.mx.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class RolesUpdateRequest {
    public List<@NotBlank String> roles;
}

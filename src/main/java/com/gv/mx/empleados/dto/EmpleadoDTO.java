// src/main/java/com/gv/mx/empleados/dto/EmpleadoDTO.java
package com.gv.mx.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {

    private Long id;

    @NotBlank(message = "El número de empleado es obligatorio")
    @Size(max = 20, message = "El número de empleado no debe exceder 20 caracteres")
    private String numEmpleado;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 120, message = "Nombres demasiado largos")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 120, message = "Apellido paterno demasiado largo")
    private String apellidoPaterno;

    @Size(max = 120, message = "Apellido materno demasiado largo")
    private String apellidoMaterno;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 200, message = "Email demasiado largo")
    private String email;

    @NotNull(message = "El estado 'activo' es obligatorio")
    private Boolean activo;
}

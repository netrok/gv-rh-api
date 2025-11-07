// src/main/java/com/gv/mx/empleados/dto/EmpleadoDTO.java
package com.gv.mx.empleados.dto;

public record EmpleadoDTO(
        Long id,
        String numEmpleado,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        Boolean activo
) {}

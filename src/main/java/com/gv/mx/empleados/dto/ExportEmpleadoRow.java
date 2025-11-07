package com.gv.mx.empleados.dto;

import java.time.LocalDate;

public record ExportEmpleadoRow(
        Long id,
        String numEmpleado,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String departamento,
        String puesto,
        LocalDate fechaIngreso,
        Boolean activo
) {}

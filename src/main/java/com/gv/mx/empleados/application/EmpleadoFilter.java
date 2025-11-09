// src/main/java/com/gv/mx/empleados/application/EmpleadoFilter.java
package com.gv.mx.empleados.application;

import java.time.LocalDate;

public record EmpleadoFilter(
        String q,
        Long departamentoId,
        Long puestoId,
        Boolean activo,
        LocalDate fechaIngresoDesde,
        LocalDate fechaIngresoHasta
) {}
package com.gv.mx.empleados.infrastructure.projections;

import java.time.LocalDate;

public interface ExportEmpleadoProjectionWithNames {
    Long getId();
    String getNumEmpleado();
    String getNombres();
    String getApellidoPaterno();
    String getApellidoMaterno();
    String getEmail();
    String getDepartamentoNombre();
    String getPuestoNombre();
    LocalDate getFechaIngreso();
    Boolean getActivo();
}

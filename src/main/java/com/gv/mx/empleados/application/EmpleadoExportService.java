// src/main/java/com/gv/mx/empleados/application/EmpleadoExportService.java
package com.gv.mx.empleados.application;

public interface EmpleadoExportService {
    byte[] exportXlsx(EmpleadoFilter f);
    byte[] exportPdf(EmpleadoFilter f);
}
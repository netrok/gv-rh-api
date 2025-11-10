// src/main/java/com/gv/mx/asistencias/mapper/AsistenciasMapper.java
package com.gv.mx.asistencias.mapper;

import com.gv.mx.asistencias.domain.Asistencia;
import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.dto.AsistenciaDTO;
import com.gv.mx.asistencias.dto.ChecadaDTO;
import org.springframework.stereotype.Component;

@Component
public class AsistenciasMapper {

    // --- Checada -> ChecadaDTO (record) ---
    public ChecadaDTO toDto(Checada c) {
        if (c == null) return null;

        // tipo (enum o String) -> String
        String tipoStr = null;
        Object tipo = safe(() -> c.getTipo()); // usa getTipo(); si tu entity usa otro nombre, me dices
        if (tipo instanceof Enum<?> e) tipoStr = e.name();
        else if (tipo != null) tipoStr = tipo.toString();

        // empleadoId directo; si tienes ManyToOne empleado, cambia a: c.getEmpleado() != null ? c.getEmpleado().getId() : null
        Long empId = safe(() -> c.getEmpleadoId());

        return new ChecadaDTO(
                empId,
                tipoStr,
                c.getFechaHora(),
                c.getDispositivo(),
                c.getUbicacion()
        );
    }

    // --- Asistencia -> AsistenciaDTO (record) ---
    public AsistenciaDTO toDto(Asistencia a) {
        if (a == null) return null;

        String estado = a.getEstado() != null ? a.getEstado().name() : null;

        // empleadoId directo; si tienes ManyToOne empleado, cambia a: a.getEmpleado() != null ? a.getEmpleado().getId() : null
        Long empId = safe(() -> a.getEmpleadoId());

        // Estos campos aún no existen en tu entity -> nuléalos
        Integer minutosRetardo = null;
        Integer minutosAnticipoSalida = null;
        String notas = null;

        return new AsistenciaDTO(
                empId,
                a.getFecha(),
                estado,
                minutosRetardo,
                minutosAnticipoSalida,
                notas
        );
    }

    // helper
    private static <T> T safe(SupplierLike<T> s) {
        try { return s.get(); } catch (Throwable ignored) { return null; }
    }
    @FunctionalInterface private interface SupplierLike<T> { T get(); }
}

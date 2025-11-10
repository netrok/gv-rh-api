// src/main/java/com/gv/mx/asistencias/mapper/AsistenciasMapper.java
package com.gv.mx.asistencias.mapper;

import com.gv.mx.asistencias.domain.Asistencia;
import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.dto.AsistenciaDTO;
import com.gv.mx.asistencias.dto.ChecadaDTO;

public final class AsistenciasMapper {
    private AsistenciasMapper() {}

    public static ChecadaDTO toDTO(Checada c) {
        // Usamos getTipo() en lugar de getTipoChecada()
        var tipo = (c.getTipo() != null) ? c.getTipo().name() : null;

        return new ChecadaDTO(
                c.getEmpleadoId(),
                tipo,
                c.getFechaHora(),
                c.getDispositivo(),
                c.getUbicacion()
        );
    }

    public static AsistenciaDTO toDTO(Asistencia a) {
        // Campos opcionales enviados como null si aún no existen en la entidad
        Integer minutosRetardo = null;
        Integer minutosAnticipoSalida = null;
        String notas = null;

        return new AsistenciaDTO(
                a.getEmpleadoId(),
                a.getFecha(),
                (a.getEstado() != null) ? a.getEstado().name() : null,
                minutosRetardo,
                minutosAnticipoSalida,
                notas
        );
    }
}

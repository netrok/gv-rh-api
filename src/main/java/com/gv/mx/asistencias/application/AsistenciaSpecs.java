package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Asistencia;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AsistenciaSpecs {
    private AsistenciaSpecs(){}

    public static Specification<Asistencia> from(AsistenciaFilter f) {
        List<Specification<Asistencia>> ands = new ArrayList<>();

        if (f.empleadoId() != null) {
            ands.add((root, q, cb) -> cb.equal(root.get("empleadoId"), f.empleadoId()));
        }
        if (f.fechaDesde() != null) {
            ands.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), f.fechaDesde()));
        }
        if (f.fechaHasta() != null) {
            ands.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), f.fechaHasta()));
        }
        if (f.estado() != null) {
            ands.add((root, q, cb) -> cb.equal(root.get("estado"), f.estado()));
        }

        return Specification.allOf(ands);
    }
}

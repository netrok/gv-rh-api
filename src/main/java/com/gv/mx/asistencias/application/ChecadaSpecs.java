package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.domain.ChecadaTipo;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ChecadaSpecs {
    private ChecadaSpecs(){}

    public static Specification<Checada> from(ChecadaFilter f) {
        List<Specification<Checada>> ands = new ArrayList<>();

        if (f.empleadoId() != null) {
            ands.add((root, q, cb) -> cb.equal(root.get("empleadoId"), f.empleadoId()));
        }
        if (f.desde() != null) {
            ands.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fechaHora"), f.desde()));
        }
        if (f.hasta() != null) {
            ands.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("fechaHora"), f.hasta()));
        }
        if (f.tipo() != null && !f.tipo().isBlank()) {
            ands.add((root, q, cb) -> cb.equal(root.get("tipo"), ChecadaTipo.valueOf(f.tipo())));
        }

        return Specification.allOf(ands);
    }
}

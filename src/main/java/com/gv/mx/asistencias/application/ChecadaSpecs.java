package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Checada;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ChecadaSpecs {
    private ChecadaSpecs(){}

    public static Specification<Checada> conFiltros(ChecadaFilter f) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (f.empleadoId() != null) {
                preds.add(cb.equal(root.get("empleadoId"), f.empleadoId()));
            }
            if (f.desde() != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), f.desde()));
            }
            if (f.hasta() != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("fechaHora"), f.hasta()));
            }
            if (f.tipo() != null) {
                preds.add(cb.equal(root.get("tipo"), f.tipo())); // enum o string, JPA lo resuelve
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
    }

    public static Specification<Checada> from(ChecadaFilter f) {
        return conFiltros(f);
    }
}

package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Asistencia;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class AsistenciaSpecs {

    private AsistenciaSpecs() {}

    public static Specification<Asistencia> conFiltros(AsistenciaFilter f) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            // ⚠️ Si tu entidad tiene ManyToOne Empleado empleado -> usar "empleado.id"
            // ⚠️ Si en cambio tienes Long empleadoId -> cambia a root.get("empleadoId")
            if (f.empleadoId() != null) {
                preds.add(cb.equal(root.get("empleado").get("id"), f.empleadoId()));
                // Alternativa si usas campo simple:
                // preds.add(cb.equal(root.get("empleadoId"), f.empleadoId()));
            }

            if (f.desde() != null) {
                // campo fecha: LocalDate en la entidad Asistencia
                preds.add(cb.greaterThanOrEqualTo(root.get("fecha"), f.desde()));
            }

            if (f.hasta() != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("fecha"), f.hasta()));
            }

            if (f.estado() != null) {
                preds.add(cb.equal(root.get("estado"), f.estado()));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}

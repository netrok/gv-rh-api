// src/main/java/com/gv/mx/empleados/infrastructure/EmpleadoSpecs.java
package com.gv.mx.empleados.infrastructure;

import com.gv.mx.empleados.application.EmpleadoFilter;
import com.gv.mx.empleados.domain.Empleado;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class EmpleadoSpecs {
    private EmpleadoSpecs() {}

    public static Specification<Empleado> from(EmpleadoFilter f) {
        // Punto de inicio ‘neutral’ (TRUE) para evitar Specification.where(...)
        Specification<Empleado> spec = (root, query, cb) -> cb.conjunction();
        if (f == null) return spec;

        // q: busca en varios campos
        if (f.q() != null && !f.q().isBlank()) {
            String like = "%" + f.q().toLowerCase(Locale.ROOT) + "%";
            Specification<Empleado> texto = (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("numEmpleado")), like),
                    cb.like(cb.lower(root.get("nombres")), like),
                    cb.like(cb.lower(root.get("apellidoPaterno")), like),
                    cb.like(cb.lower(root.get("apellidoMaterno")), like),
                    cb.like(cb.lower(root.get("email")), like)
            );
            spec = spec.and(texto);
        }

        if (f.departamentoId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("departamentoId"), f.departamentoId()));
        }

        if (f.puestoId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("puestoId"), f.puestoId()));
        }

        if (f.activo() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), f.activo()));
        }

        if (f.fechaIngresoDesde() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(
                    root.get("fechaIngreso"), f.fechaIngresoDesde()));
        }

        if (f.fechaIngresoHasta() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(
                    root.get("fechaIngreso"), f.fechaIngresoHasta()));
        }

        return spec;
    }
}
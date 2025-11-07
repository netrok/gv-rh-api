package com.gv.mx.empleados.infrastructure;

import com.gv.mx.empleados.domain.Empleado;
import com.gv.mx.empleados.infrastructure.projections.ExportEmpleadoProjectionWithNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query(value = """
        SELECT
          e.id,
          e.num_empleado       AS numEmpleado,
          e.nombres            AS nombres,
          e.apellido_paterno   AS apellidoPaterno,
          e.apellido_materno   AS apellidoMaterno,
          e.email              AS email,
          d.nombre             AS departamentoNombre,
          p.nombre             AS puestoNombre,
          e.fecha_ingreso      AS fechaIngreso,
          e.activo             AS activo
        FROM empleados e
        LEFT JOIN departamentos d ON d.id = e.departamento_id
        LEFT JOIN puestos       p ON p.id = e.puesto_id
        WHERE (:q IS NULL OR
               LOWER(CONCAT_WS(' ',
                 e.num_empleado, e.nombres, e.apellido_paterno, e.apellido_materno, e.email
               )) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:departamentoId IS NULL OR e.departamento_id = :departamentoId)
          AND (:puestoId       IS NULL OR e.puesto_id       = :puestoId)
          AND (:activo         IS NULL OR e.activo          = :activo)
        ORDER BY e.num_empleado ASC
        """, nativeQuery = true)
    List<ExportEmpleadoProjectionWithNames> findForExport(
            @Param("q") String q,
            @Param("departamentoId") Long departamentoId,
            @Param("puestoId") Long puestoId,
            @Param("activo") Boolean activo
    );
}

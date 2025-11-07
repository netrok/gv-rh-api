// src/main/java/com/gv/mx/empleados/infrastructure/EmpleadoRepository.java
package com.gv.mx.empleados.infrastructure;

import com.gv.mx.empleados.domain.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("""
        SELECT e FROM Empleado e
        WHERE
            LOWER(e.numEmpleado)      LIKE LOWER(CONCAT('%', :t, '%')) OR
            LOWER(e.nombres)          LIKE LOWER(CONCAT('%', :t, '%')) OR
            LOWER(e.apellidoPaterno)  LIKE LOWER(CONCAT('%', :t, '%')) OR
            LOWER(e.apellidoMaterno)  LIKE LOWER(CONCAT('%', :t, '%')) OR
            LOWER(e.email)            LIKE LOWER(CONCAT('%', :t, '%'))
    """)
    Page<Empleado> search(@Param("t") String texto, Pageable pageable);
}

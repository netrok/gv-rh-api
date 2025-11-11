// src/main/java/com/gv/mx/vacaciones/infrastructure/SolicitudVacacionesRepo.java
package com.gv.mx.vacaciones.infrastructure;

import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import com.gv.mx.vacaciones.domain.VacacionEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SolicitudVacacionesRepo extends JpaRepository<SolicitudVacaciones, Long> {

    @Query("""
        select s
        from SolicitudVacaciones s
        where s.empleadoId = :empleadoId
          and s.estado = :estado
          and s.fechaHasta >= :from
          and s.fechaDesde <= :to
    """)
    List<SolicitudVacaciones> findByEmpleadoAndRangeAndEstado(
            @Param("empleadoId") Long empleadoId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("estado") VacacionEstado estado
    );

    // Azúcar sintáctica para aprobadas
    default List<SolicitudVacaciones> findAprobadasByEmpleadoAndRange(Long empleadoId, LocalDate from, LocalDate to) {
        return findByEmpleadoAndRangeAndEstado(empleadoId, from, to, VacacionEstado.APROBADA);
        // si prefieres incluir CANCELADA o RECHAZADA, agrega métodos similares
    }
}

// IncidenciaRepository.java
package com.gv.mx.incidencias.infrastructure;

import com.gv.mx.incidencias.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    Page<Incidencia> findByEmpleadoId(Long empleadoId, Pageable pageable);
    long countByEstado(IncidenciaEstado estado);
    long countByTipo(IncidenciaTipo tipo);
}

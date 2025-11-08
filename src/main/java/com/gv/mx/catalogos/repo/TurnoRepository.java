package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Turno;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    Page<Turno> findByNombreContainingIgnoreCase(String q, Pageable pageable);
}
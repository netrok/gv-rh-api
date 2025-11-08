package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Puesto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuestoRepository extends JpaRepository<Puesto, Long> {
    Page<Puesto> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    Page<Puesto> findByDepartamento_IdAndNombreContainingIgnoreCase(Long depId, String q, Pageable pageable);
}
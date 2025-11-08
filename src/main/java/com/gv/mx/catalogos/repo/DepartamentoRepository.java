package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Departamento;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    Page<Departamento> findByNombreContainingIgnoreCase(String q, Pageable pageable);
}
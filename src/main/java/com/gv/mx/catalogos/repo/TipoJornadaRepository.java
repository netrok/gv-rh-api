package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.TipoJornada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoJornadaRepository extends JpaRepository<TipoJornada, Long> {
    Page<TipoJornada> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    boolean existsByNombreIgnoreCase(String nombre);
}
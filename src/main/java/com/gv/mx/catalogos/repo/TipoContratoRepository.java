package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.TipoContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
    Page<TipoContrato> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id); // <-- requerido por el service
}
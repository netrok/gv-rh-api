package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Puesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    // Listado / filtros
    Page<Puesto> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    Page<Puesto> findByDepartamentoId(Long departamentoId, Pageable pageable);
    Page<Puesto> findByNombreContainingIgnoreCaseAndDepartamentoId(String q, Long departamentoId, Pageable pageable);

    // Duplicados (único por departamento + nombre)
    boolean existsByDepartamentoIdAndNombreIgnoreCase(Long departamentoId, String nombre);
    boolean existsByDepartamentoIdAndNombreIgnoreCaseAndIdNot(Long departamentoId, String nombre, Long id);
}
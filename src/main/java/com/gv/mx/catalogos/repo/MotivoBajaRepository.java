// src/main/java/com/gv/mx/catalogos/repo/MotivoBajaRepository.java
package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.MotivoBaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotivoBajaRepository extends JpaRepository<MotivoBaja, Long> {
    Page<MotivoBaja> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Horario;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    Page<Horario> findByNombreContainingIgnoreCase(String q, Pageable pageable);
}
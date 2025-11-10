package com.gv.mx.asistencias.repo;

import com.gv.mx.asistencias.domain.Checada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChecadaRepository extends JpaRepository<Checada, Long>, JpaSpecificationExecutor<Checada> {
}

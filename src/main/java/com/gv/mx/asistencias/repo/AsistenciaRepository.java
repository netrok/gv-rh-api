package com.gv.mx.asistencias.repo;

import com.gv.mx.asistencias.domain.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AsistenciaRepository extends
        org.springframework.data.jpa.repository.JpaRepository<Asistencia, Long>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Asistencia> {}


package com.gv.mx.vacaciones.infrastructure;

import com.gv.mx.vacaciones.domain.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    boolean existsByFecha(LocalDate fecha);
    List<Feriado> findByFechaBetween(LocalDate desde, LocalDate hasta);
}

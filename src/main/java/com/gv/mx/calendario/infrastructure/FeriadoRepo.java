package com.gv.mx.calendario.infrastructure;

import com.gv.mx.calendario.domain.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeriadoRepo extends JpaRepository<Feriado, Long> {
    Optional<Feriado> findByFecha(LocalDate fecha);
    List<Feriado> findByFechaBetween(LocalDate desde, LocalDate hasta);
    boolean existsByFecha(LocalDate fecha);
}

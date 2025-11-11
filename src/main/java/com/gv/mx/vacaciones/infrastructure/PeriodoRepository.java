package com.gv.mx.vacaciones.infrastructure;
import com.gv.mx.vacaciones.domain.*; import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface PeriodoRepository extends JpaRepository<PeriodoVacacional, Long> {
    Optional<PeriodoVacacional> findByAnio(Integer anio);
}
package com.gv.mx.vacaciones.infrastructure;
import com.gv.mx.vacaciones.domain.*; import org.springframework.data.jpa.repository.*;
import java.util.*;


public interface SaldoRepository extends JpaRepository<SaldoVacaciones, Long> {
    Optional<SaldoVacaciones> findByEmpleadoIdAndAnio(Long empleadoId, Integer anio);
}

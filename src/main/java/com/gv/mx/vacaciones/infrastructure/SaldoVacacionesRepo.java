package com.gv.mx.vacaciones.infrastructure;

import com.gv.mx.vacaciones.domain.SaldoVacaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaldoVacacionesRepo extends JpaRepository<SaldoVacaciones, Long> {
    @Query("select s.diasAsignados from SaldoVacaciones s where s.empleadoId = :empleadoId and s.anio = :anio")
    Integer findDiasAsignados(Long empleadoId, int anio);
}

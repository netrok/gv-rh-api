package com.gv.mx.vacaciones.infrastructure;
import com.gv.mx.vacaciones.domain.*; import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface SolicitudRepository extends JpaRepository<SolicitudVacaciones, Long> {
    long countByEmpleadoIdAndEstado(Long empleadoId, VacacionEstado estado);
}

// VacacionesMapper.java
package com.gv.mx.vacaciones.mapper;

import com.gv.mx.vacaciones.domain.*;
import com.gv.mx.vacaciones.dto.*;
import org.springframework.stereotype.Component;

@Component
public class VacacionesMapper {
    public PeriodoDTO toDto(PeriodoVacacional p){ return new PeriodoDTO(p.getId(), p.getAnio(), p.getFechaInicio(), p.getFechaFin()); }
    public PeriodoVacacional toEntity(PeriodoDTO d){ var p=new PeriodoVacacional(); p.setId(d.id()); p.setAnio(d.anio()); p.setFechaInicio(d.fechaInicio()); p.setFechaFin(d.fechaFin()); return p; }

    public SaldoDTO toDto(SaldoVacaciones s){ return new SaldoDTO(s.getId(), s.getEmpleadoId(), s.getAnio(), s.getDiasAsignados(), s.getDiasDisfrutados(), s.getDiasPendientes()); }

    public SolicitudDTO toDto(SolicitudVacaciones s){
        return new SolicitudDTO(s.getId(), s.getEmpleadoId(), s.getAnio(), s.getFechaDesde(), s.getFechaHasta(), s.getDias(),
                s.getEstado()!=null? s.getEstado().name():null, s.getMotivo(), s.getAdjunto());
    }
}

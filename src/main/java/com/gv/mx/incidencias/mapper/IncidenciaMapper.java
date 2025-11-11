// IncidenciaMapper.java
package com.gv.mx.incidencias.mapper;

import com.gv.mx.incidencias.domain.*;
import com.gv.mx.incidencias.dto.IncidenciaDTO;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaMapper {
    public IncidenciaDTO toDto(Incidencia i){
        return new IncidenciaDTO(
                i.getId(), i.getEmpleadoId(),
                i.getTipo()!=null? i.getTipo().name():null,
                i.getEstado()!=null? i.getEstado().name():null,
                i.getFecha(), i.getMinutos(), i.getMotivo(), i.getAdjunto()
        );
    }
    public Incidencia toEntity(IncidenciaDTO d){
        var i = new Incidencia();
        i.setId(d.id());
        i.setEmpleadoId(d.empleadoId());
        if(d.tipo()!=null)   i.setTipo(IncidenciaTipo.valueOf(d.tipo()));
        if(d.estado()!=null) i.setEstado(IncidenciaEstado.valueOf(d.estado()));
        i.setFecha(d.fecha());
        i.setMinutos(d.minutos());
        i.setMotivo(d.motivo());
        i.setAdjunto(d.adjunto());
        return i;
    }
    public void merge(Incidencia t, IncidenciaDTO d){
        if(d.empleadoId()!=null) t.setEmpleadoId(d.empleadoId());
        if(d.tipo()!=null)       t.setTipo(IncidenciaTipo.valueOf(d.tipo()));
        if(d.estado()!=null)     t.setEstado(IncidenciaEstado.valueOf(d.estado()));
        if(d.fecha()!=null)      t.setFecha(d.fecha());
        if(d.minutos()!=null)    t.setMinutos(d.minutos());
        if(d.motivo()!=null)     t.setMotivo(d.motivo());
        if(d.adjunto()!=null)    t.setAdjunto(d.adjunto());
    }
}

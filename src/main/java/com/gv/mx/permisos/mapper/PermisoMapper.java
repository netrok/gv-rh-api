package com.gv.mx.permisos.mapper;

import com.gv.mx.permisos.domain.*;
import com.gv.mx.permisos.dto.PermisoDTO;
import org.springframework.stereotype.Component;

@Component
public class PermisoMapper {

    public PermisoDTO toDto(Permiso p){
        return new PermisoDTO(
                p.getId(), p.getEmpleadoId(),
                p.getTipo() != null ? p.getTipo().name() : null,
                p.getEstado() != null ? p.getEstado().name() : null,
                p.getFechaDesde(), p.getFechaHasta(),
                p.getHoras(), p.getMotivo(), p.getAdjunto()
        );
    }

    public Permiso toEntity(PermisoDTO d){
        Permiso p = new Permiso();
        p.setId(d.id());
        p.setEmpleadoId(d.empleadoId());
        if (d.tipo() != null)   p.setTipo(PermisoTipo.valueOf(d.tipo()));
        if (d.estado() != null) p.setEstado(PermisoEstado.valueOf(d.estado()));
        p.setFechaDesde(d.fechaDesde());
        p.setFechaHasta(d.fechaHasta());
        p.setHoras(d.horas());
        p.setMotivo(d.motivo());
        p.setAdjunto(d.adjunto());
        return p;
    }

    public void merge(Permiso target, PermisoDTO d){
        if (d.empleadoId() != null) target.setEmpleadoId(d.empleadoId());
        if (d.tipo() != null)       target.setTipo(PermisoTipo.valueOf(d.tipo()));
        if (d.estado() != null)     target.setEstado(PermisoEstado.valueOf(d.estado()));
        if (d.fechaDesde() != null) target.setFechaDesde(d.fechaDesde());
        if (d.fechaHasta() != null) target.setFechaHasta(d.fechaHasta());
        if (d.horas() != null)      target.setHoras(d.horas());
        if (d.motivo() != null)     target.setMotivo(d.motivo());
        if (d.adjunto() != null)    target.setAdjunto(d.adjunto());
    }
}

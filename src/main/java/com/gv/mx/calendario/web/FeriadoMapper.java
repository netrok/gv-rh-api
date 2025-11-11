package com.gv.mx.calendario.web;

import com.gv.mx.calendario.domain.Feriado;
import com.gv.mx.calendario.web.dto.FeriadoDTO;
import org.springframework.stereotype.Component;

@Component
public class FeriadoMapper {
    public FeriadoDTO toDto(Feriado f) {
        return new FeriadoDTO(f.getId(), f.getFecha(), f.getDescripcion(), f.isNacional());
    }
    public Feriado toEntity(FeriadoDTO d) {
        Feriado f = new Feriado();
        f.setId(d.id());
        f.setFecha(d.fecha());
        f.setDescripcion(d.descripcion());
        f.setNacional(Boolean.TRUE.equals(d.nacional()));
        return f;
    }
}

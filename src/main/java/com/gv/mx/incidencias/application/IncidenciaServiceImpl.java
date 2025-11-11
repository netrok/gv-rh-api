// IncidenciaServiceImpl.java
package com.gv.mx.incidencias.application;

import com.gv.mx.incidencias.domain.*;
import com.gv.mx.incidencias.dto.IncidenciaDTO;
import com.gv.mx.incidencias.infrastructure.IncidenciaRepository;
import com.gv.mx.incidencias.mapper.IncidenciaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {
    private final IncidenciaRepository repo;
    private final IncidenciaMapper mapper;
    public IncidenciaServiceImpl(IncidenciaRepository r, IncidenciaMapper m){ this.repo=r; this.mapper=m; }

    @Override public Page<IncidenciaDTO> listar(Long empleadoId, Pageable p){
        return (empleadoId==null? repo.findAll(p): repo.findByEmpleadoId(empleadoId,p)).map(mapper::toDto);
    }
    @Override public IncidenciaDTO obtener(Long id){ return mapper.toDto(repo.findById(id).orElseThrow()); }
    @Override public IncidenciaDTO crear(IncidenciaDTO dto){
        var i = mapper.toEntity(dto);
        if(i.getEstado()==null) i.setEstado(IncidenciaEstado.PENDIENTE);
        return mapper.toDto(repo.save(i));
    }
    @Override public IncidenciaDTO actualizar(Long id, IncidenciaDTO dto){
        var i = repo.findById(id).orElseThrow();
        mapper.merge(i, dto);
        return mapper.toDto(repo.save(i));
    }
    @Override public void eliminar(Long id){ repo.deleteById(id); }
    @Override public IncidenciaDTO aprobar(Long id){ return setEstado(id, IncidenciaEstado.APROBADA); }
    @Override public IncidenciaDTO rechazar(Long id){ return setEstado(id, IncidenciaEstado.RECHAZADA); }
    @Override public IncidenciaDTO cancelar(Long id){ return setEstado(id, IncidenciaEstado.CANCELADA); }

    private IncidenciaDTO setEstado(Long id, IncidenciaEstado e){
        var i = repo.findById(id).orElseThrow();
        i.setEstado(e);
        return mapper.toDto(repo.save(i));
    }
}

package com.gv.mx.permisos.application;

import com.gv.mx.permisos.domain.Permiso;
import com.gv.mx.permisos.domain.PermisoEstado;
import com.gv.mx.permisos.dto.PermisoDTO;
import com.gv.mx.permisos.infrastructure.PermisoRepository;
import com.gv.mx.permisos.mapper.PermisoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository repo;
    private final PermisoMapper mapper;

    public PermisoServiceImpl(PermisoRepository repo, PermisoMapper mapper) {
        this.repo = repo; this.mapper = mapper;
    }

    @Override
    public Page<PermisoDTO> listar(Long empleadoId, Pageable pageable) {
        return (empleadoId == null ? repo.findAll(pageable) : repo.findByEmpleadoId(empleadoId, pageable))
                .map(mapper::toDto);
    }

    @Override
    public PermisoDTO obtener(Long id) {
        return mapper.toDto(repo.findById(id).orElseThrow());
    }

    @Override
    public PermisoDTO crear(PermisoDTO dto) {
        Permiso p = mapper.toEntity(dto);
        p.setEstado(p.getEstado() == null ? PermisoEstado.PENDIENTE : p.getEstado());
        return mapper.toDto(repo.save(p));
    }

    @Override
    public PermisoDTO actualizar(Long id, PermisoDTO dto) {
        Permiso p = repo.findById(id).orElseThrow();
        mapper.merge(p, dto);
        return mapper.toDto(repo.save(p));
    }

    @Override
    public void eliminar(Long id) { repo.deleteById(id); }

    @Override public PermisoDTO aprobar(Long id){ return changeState(id, PermisoEstado.APROBADO); }
    @Override public PermisoDTO rechazar(Long id){ return changeState(id, PermisoEstado.RECHAZADO); }
    @Override public PermisoDTO cancelar(Long id){ return changeState(id, PermisoEstado.CANCELADO); }

    private PermisoDTO changeState(Long id, PermisoEstado nuevo){
        Permiso p = repo.findById(id).orElseThrow();
        p.setEstado(nuevo);
        return mapper.toDto(repo.save(p));
    }
}

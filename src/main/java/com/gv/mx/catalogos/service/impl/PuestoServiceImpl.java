package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Puesto;
import com.gv.mx.catalogos.repo.PuestoRepository;
import com.gv.mx.catalogos.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PuestoServiceImpl implements PuestoService {

    private final PuestoRepository repo;

    @Transactional(readOnly = true)
    public Page<Puesto> listar(String q, Long departamentoId, Pageable pageable) {
        boolean hasQ = q != null && !q.isBlank();
        if (departamentoId != null) {
            return repo.findByDepartamento_IdAndNombreContainingIgnoreCase(departamentoId, hasQ ? q : "", pageable);
        }
        return hasQ ? repo.findByNombreContainingIgnoreCase(q, pageable) : repo.findAll(pageable);
    }

    public Puesto crear(Puesto p) {
        p.setId(null);
        if (p.getActivo() == null) p.setActivo(true);
        return repo.save(p);
    }

    public Puesto actualizar(Long id, Puesto p) {
        Puesto db = obtener(id);
        if (p.getDepartamento() != null) db.setDepartamento(p.getDepartamento());
        db.setNombre(p.getNombre());
        if (p.getActivo() != null) db.setActivo(p.getActivo());
        return repo.save(db);
    }

    public void desactivar(Long id) {
        Puesto db = obtener(id);
        db.setActivo(false);
        repo.save(db);
    }

    @Transactional(readOnly = true)
    public Puesto obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));
    }
}
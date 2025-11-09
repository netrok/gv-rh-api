package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Departamento;
import com.gv.mx.catalogos.domain.Puesto;
import com.gv.mx.catalogos.repo.DepartamentoRepository;
import com.gv.mx.catalogos.repo.PuestoRepository;
import com.gv.mx.catalogos.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional
public class PuestoServiceImpl implements PuestoService {

    private final PuestoRepository repo;
    private final DepartamentoRepository depRepo;

    @Override @Transactional(readOnly = true)
    public Page<Puesto> listar(String q, Long departamentoId, Pageable pageable) {
        boolean noQ = (q == null || q.isBlank());
        if (departamentoId != null) {
            return noQ
                    ? repo.findByDepartamentoId(departamentoId, pageable)
                    : repo.findByNombreContainingIgnoreCaseAndDepartamentoId(q, departamentoId, pageable);
        }
        return noQ ? repo.findAll(pageable) : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    // Si tu interfaz solo expone listar(q,pageable), deja este overload:
    @Transactional(readOnly = true)
    public Page<Puesto> listar(String q, Pageable pageable) {
        return listar(q, null, pageable);
    }

    @Override @Transactional(readOnly = true)
    public Puesto obtener(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public Puesto crear(Puesto body) {
        body.setId(null);
        // validar departamento
        Long depId = body.getDepartamento() != null ? body.getDepartamento().getId() : null;
        if (depId == null) throw new IllegalArgumentException("departamento requerido");

        var dep = depRepo.findById(depId).orElseThrow(() -> new IllegalArgumentException("departamento inválido"));

        if (repo.existsByDepartamentoIdAndNombreIgnoreCase(dep.getId(), body.getNombre()))
            throw new IllegalArgumentException("nombre duplicado en ese departamento");

        body.setDepartamento(dep);
        body.setActivo(Boolean.TRUE);
        return repo.save(body);
    }

    @Override
    public Puesto actualizar(Long id, Puesto body) {
        var cur = obtener(id);

        Long depId = body.getDepartamento() != null ? body.getDepartamento().getId() : null;
        if (depId == null) depId = (cur.getDepartamento() != null ? cur.getDepartamento().getId() : null);
        if (depId == null) throw new IllegalArgumentException("departamento requerido");

        // validar duplicado (mismo dep + nombre, distinto id)
        if (repo.existsByDepartamentoIdAndNombreIgnoreCaseAndIdNot(depId, body.getNombre(), id))
            throw new IllegalArgumentException("nombre duplicado en ese departamento");

        // reasignar dep si viene distinto
        if (body.getDepartamento() != null && !depId.equals(cur.getDepartamento().getId())) {
            var dep = depRepo.findById(depId).orElseThrow(() -> new IllegalArgumentException("departamento inválido"));
            cur.setDepartamento(dep);
        }

        cur.setNombre(body.getNombre());
        if (body.getActivo() != null) cur.setActivo(body.getActivo());
        return repo.save(cur);
    }

    @Override
    public void desactivar(Long id) {
        var cur = obtener(id);
        cur.setActivo(Boolean.FALSE);
        repo.save(cur);
    }
}
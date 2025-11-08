package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Departamento;
import com.gv.mx.catalogos.repo.DepartamentoRepository;
import com.gv.mx.catalogos.service.DepartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoRepository repo;

    @Transactional(readOnly = true)
    public Page<Departamento> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    public Departamento crear(Departamento d) {
        d.setId(null);
        if (d.getActivo() == null) d.setActivo(true);
        return repo.save(d);
    }

    public Departamento actualizar(Long id, Departamento d) {
        Departamento db = obtener(id);
        db.setNombre(d.getNombre());
        if (d.getActivo() != null) db.setActivo(d.getActivo());
        return repo.save(db);
    }

    public void desactivar(Long id) {
        Departamento db = obtener(id);
        db.setActivo(false);
        repo.save(db);
    }

    @Transactional(readOnly = true)
    public Departamento obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));
    }
}
package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Banco;
import com.gv.mx.catalogos.repo.BancoRepository;
import com.gv.mx.catalogos.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BancoServiceImpl implements BancoService {

    private final BancoRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Page<Banco> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Banco obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado: " + id));
    }

    @Override
    @Transactional
    public Banco guardar(Banco data) {
        if (repo.existsByNombreIgnoreCase(data.getNombre())) {
            throw new IllegalArgumentException("Ya existe banco con nombre: " + data.getNombre());
        }
        data.setId(null);
        return repo.save(data);
    }

    @Override
    @Transactional
    public Banco actualizar(Long id, Banco data) {
        var entity = obtener(id);
        var nuevoNombre = data.getNombre();
        if (!entity.getNombre().equalsIgnoreCase(nuevoNombre)
                && repo.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new IllegalArgumentException("Ya existe banco con nombre: " + nuevoNombre);
        }
        entity.setNombre(nuevoNombre);
        entity.setActivo(data.isActivo());
        return repo.save(entity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (repo.existsById(id)) repo.deleteById(id);
    }
}
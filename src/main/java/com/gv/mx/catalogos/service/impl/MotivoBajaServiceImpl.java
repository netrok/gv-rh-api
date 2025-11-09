// src/main/java/com/gv/mx/catalogos/service/impl/MotivoBajaServiceImpl.java
package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.MotivoBaja;
import com.gv.mx.catalogos.repo.MotivoBajaRepository;
import com.gv.mx.catalogos.service.MotivoBajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MotivoBajaServiceImpl implements MotivoBajaService {

    private final MotivoBajaRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Page<MotivoBaja> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public MotivoBaja obtener(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public MotivoBaja crear(MotivoBaja body) {
        if (repo.existsByNombreIgnoreCase(body.getNombre())) {
            throw new IllegalArgumentException("nombre duplicado");
        }
        body.setId(null);
        if (body.getActivo() == null) body.setActivo(Boolean.TRUE);
        return repo.save(body);
    }

    @Override
    public MotivoBaja actualizar(Long id, MotivoBaja body) {
        var cur = obtener(id);

        if (repo.existsByNombreIgnoreCaseAndIdNot(body.getNombre(), id)) {
            throw new IllegalArgumentException("nombre duplicado");
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
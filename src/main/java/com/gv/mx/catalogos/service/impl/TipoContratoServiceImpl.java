package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.TipoContrato;
import com.gv.mx.catalogos.repo.TipoContratoRepository;
import com.gv.mx.catalogos.service.TipoContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoContratoServiceImpl implements TipoContratoService {

    private final TipoContratoRepository repo;

    @Override @Transactional(readOnly = true)
    public Page<TipoContrato> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    @Override @Transactional(readOnly = true)
    public TipoContrato obtener(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public TipoContrato crear(TipoContrato body) {
        if (repo.existsByNombreIgnoreCase(body.getNombre()))
            throw new IllegalArgumentException("nombre duplicado");
        body.setId(null);
        if (body.getActivo() == null) body.setActivo(Boolean.TRUE);
        return repo.save(body);
    }

    @Override
    public TipoContrato actualizar(Long id, TipoContrato body) {
        var cur = obtener(id);

        // si cambia el nombre, validamos duplicado en otros ids
        if (repo.existsByNombreIgnoreCaseAndIdNot(body.getNombre(), id))
            throw new IllegalArgumentException("nombre duplicado");

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
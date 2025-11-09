package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.TipoJornada;
import com.gv.mx.catalogos.repo.TipoJornadaRepository;
import com.gv.mx.catalogos.service.TipoJornadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoJornadaServiceImpl implements TipoJornadaService {

    private final TipoJornadaRepository repo;

    @Override @Transactional(readOnly = true)
    public Page<TipoJornada> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    @Override @Transactional(readOnly = true)
    public TipoJornada obtener(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public TipoJornada crear(TipoJornada body) {
        if (repo.existsByNombreIgnoreCase(body.getNombre()))
            throw new IllegalArgumentException("nombre duplicado");
        body.setId(null);
        if (body.getActivo() == null) body.setActivo(Boolean.TRUE);
        return repo.save(body);
    }

    @Override
    public TipoJornada actualizar(Long id, TipoJornada body) {
        var cur = obtener(id);

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
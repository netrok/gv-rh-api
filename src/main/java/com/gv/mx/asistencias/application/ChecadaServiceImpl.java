package com.gv.mx.asistencias.application.impl;

import com.gv.mx.asistencias.application.ChecadaFilter;
import com.gv.mx.asistencias.application.ChecadaService;
import com.gv.mx.asistencias.application.ChecadaSpecs;
import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.repo.ChecadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gv.mx.shared.utils.Pageables.safe;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecadaServiceImpl implements ChecadaService {

    private final ChecadaRepository repo;

    private static final Set<String> ALLOWED_SORT = Set.of(
            "id","empleadoId","tipo","fechaHora","dispositivo","createdAt"
    );

    @Override
    public Checada registrar(Checada c) {
        c.setId(null);
        return repo.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Checada> listar(ChecadaFilter f, Pageable pageable) {
        Pageable p = safe(pageable, ALLOWED_SORT, "fechaHora");
        return repo.findAll(ChecadaSpecs.from(f), p);
    }
}

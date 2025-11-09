package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Horario;
import com.gv.mx.catalogos.repo.HorarioRepository;
import com.gv.mx.catalogos.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Page<Horario> listar(String q, Pageable pageable) {
        return (q != null && !q.isBlank())
                ? repo.findByNombreContainingIgnoreCase(q, pageable)
                : repo.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Horario obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado: " + id));
    }

    @Override
    public Horario crear(Horario body) {
        if (repo.existsByNombreIgnoreCase(body.getNombre()))
            throw new IllegalArgumentException("nombre duplicado");

        body.setId(null);

        // defaults por si llegan nulos (también se cubren en @PrePersist del entity)
        if (body.getActivo() == null) body.setActivo(Boolean.TRUE);
        if (body.getLunes() == null) body.setLunes(Boolean.TRUE);
        if (body.getMartes() == null) body.setMartes(Boolean.TRUE);
        if (body.getMiercoles() == null) body.setMiercoles(Boolean.TRUE);
        if (body.getJueves() == null) body.setJueves(Boolean.TRUE);
        if (body.getViernes() == null) body.setViernes(Boolean.TRUE);
        if (body.getSabado() == null) body.setSabado(Boolean.FALSE);
        if (body.getDomingo() == null) body.setDomingo(Boolean.FALSE);
        if (body.getMinutosComida() == null) body.setMinutosComida(0);

        return repo.save(body);
    }

    @Override
    public Horario actualizar(Long id, Horario body) {
        var cur = obtener(id);

        if (repo.existsByNombreIgnoreCaseAndIdNot(body.getNombre(), id))
            throw new IllegalArgumentException("nombre duplicado");

        cur.setNombre(body.getNombre());
        if (body.getHoraEntrada() != null)   cur.setHoraEntrada(body.getHoraEntrada());
        if (body.getHoraSalida()  != null)   cur.setHoraSalida(body.getHoraSalida());
        if (body.getMinutosComida()!= null)  cur.setMinutosComida(body.getMinutosComida());

        if (body.getLunes()     != null) cur.setLunes(body.getLunes());
        if (body.getMartes()    != null) cur.setMartes(body.getMartes());
        if (body.getMiercoles() != null) cur.setMiercoles(body.getMiercoles());
        if (body.getJueves()    != null) cur.setJueves(body.getJueves());
        if (body.getViernes()   != null) cur.setViernes(body.getViernes());
        if (body.getSabado()    != null) cur.setSabado(body.getSabado());
        if (body.getDomingo()   != null) cur.setDomingo(body.getDomingo());

        if (body.getActivo()    != null) cur.setActivo(body.getActivo());

        return repo.save(cur);
    }

    @Override
    public void desactivar(Long id) {
        var cur = obtener(id);
        cur.setActivo(Boolean.FALSE);
        repo.save(cur);
    }
}
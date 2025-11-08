package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Horario;
import com.gv.mx.catalogos.repo.HorarioRepository;
import com.gv.mx.catalogos.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository repo;

    @Transactional(readOnly = true)
    public Page<Horario> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank()) ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    public Horario crear(Horario h) {
        h.setId(null);
        if (h.getActivo() == null) h.setActivo(true);
        return repo.save(h);
    }

    public Horario actualizar(Long id, Horario h) {
        Horario db = obtener(id);
        db.setNombre(h.getNombre());
        db.setHoraEntrada(h.getHoraEntrada());
        db.setHoraSalida(h.getHoraSalida());
        db.setMinutosComida(h.getMinutosComida());
        db.setLunes(h.getLunes());
        db.setMartes(h.getMartes());
        db.setMiercoles(h.getMiercoles());
        db.setJueves(h.getJueves());
        db.setViernes(h.getViernes());
        db.setSabado(h.getSabado());
        db.setDomingo(h.getDomingo());
        if (h.getActivo() != null) db.setActivo(h.getActivo());
        return repo.save(db);
    }

    public void desactivar(Long id) {
        Horario db = obtener(id);
        db.setActivo(false);
        repo.save(db);
    }

    @Transactional(readOnly = true)
    public Horario obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
    }
}
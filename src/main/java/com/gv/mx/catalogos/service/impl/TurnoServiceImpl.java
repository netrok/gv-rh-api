package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Turno;
import com.gv.mx.catalogos.repo.TurnoRepository;
import com.gv.mx.catalogos.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository repo;

    @Transactional(readOnly = true)
    public Page<Turno> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(q, pageable);
    }

    public Turno crear(Turno t) {
        t.setId(null);
        if (t.getActivo() == null) t.setActivo(true);
        return repo.save(t);
    }

    public Turno actualizar(Long id, Turno t) {
        Turno db = obtener(id);
        db.setNombre(t.getNombre());
        db.setHoraEntrada(t.getHoraEntrada());
        db.setHoraSalida(t.getHoraSalida());
        db.setToleranciaEntradaMin(t.getToleranciaEntradaMin());
        db.setToleranciaSalidaMin(t.getToleranciaSalidaMin());
        db.setVentanaInicioMin(t.getVentanaInicioMin());
        db.setVentanaFinMin(t.getVentanaFinMin());
        if (t.getActivo() != null) db.setActivo(t.getActivo());
        return repo.save(db);
    }

    public void desactivar(Long id) {
        Turno db = obtener(id);
        db.setActivo(false);
        repo.save(db);
    }

    @Transactional(readOnly = true)
    public Turno obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    }
}
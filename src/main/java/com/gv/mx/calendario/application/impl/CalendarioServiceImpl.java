package com.gv.mx.calendario.application.impl;

import com.gv.mx.calendario.application.CalendarioService;
import com.gv.mx.calendario.domain.Feriado;
import com.gv.mx.calendario.infrastructure.FeriadoRepo;
import com.gv.mx.calendario.web.FeriadoMapper;
import com.gv.mx.calendario.web.dto.FeriadoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CalendarioServiceImpl implements CalendarioService {

    private final FeriadoRepo repo;
    private final FeriadoMapper mapper;

    public CalendarioServiceImpl(FeriadoRepo repo, FeriadoMapper mapper) {
        this.repo = repo; this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public boolean esLaborable(LocalDate fecha) {
        // L-V laborables, excluir sábados/domingos y feriados registrados
        DayOfWeek d = fecha.getDayOfWeek();
        if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) return false;
        return repo.findByFecha(fecha).isEmpty();
    }

    @Transactional(readOnly = true)
    public List<FeriadoDTO> listar(LocalDate desde, LocalDate hasta) {
        var list = (desde != null && hasta != null)
                ? repo.findByFechaBetween(desde, hasta)
                : repo.findAll();
        return list.stream().map(mapper::toDto).toList();
    }

    public FeriadoDTO crear(FeriadoDTO dto) {
        if (repo.existsByFecha(dto.fecha())) throw new IllegalArgumentException("Feriado duplicado");
        Feriado f = repo.save(mapper.toEntity(dto));
        return mapper.toDto(f);
    }

    public FeriadoDTO actualizar(Long id, FeriadoDTO dto) {
        Feriado f = repo.findById(id).orElseThrow();
        f.setFecha(dto.fecha());
        f.setDescripcion(dto.descripcion());
        f.setNacional(Boolean.TRUE.equals(dto.nacional()));
        return mapper.toDto(f);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

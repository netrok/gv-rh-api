package com.gv.mx.calendario.application;

import com.gv.mx.calendario.web.dto.FeriadoDTO;

import java.time.LocalDate;
import java.util.List;

public interface CalendarioService {
    boolean esLaborable(LocalDate fecha);
    List<FeriadoDTO> listar(LocalDate desde, LocalDate hasta);
    FeriadoDTO crear(FeriadoDTO dto);
    FeriadoDTO actualizar(Long id, FeriadoDTO dto);
    void eliminar(Long id);
}

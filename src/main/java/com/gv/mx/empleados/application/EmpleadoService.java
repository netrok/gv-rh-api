package com.gv.mx.empleados.application;

import com.gv.mx.empleados.dto.EmpleadoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmpleadoService {
    Page<EmpleadoDTO> listar(EmpleadoFilter f, Pageable pageable);
    EmpleadoDTO obtener(Long id);
    EmpleadoDTO crear(EmpleadoDTO dto);
    EmpleadoDTO actualizar(Long id, EmpleadoDTO dto);
    void eliminar(Long id);
}
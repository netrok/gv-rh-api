// src/main/java/com/gv/mx/empleados/application/EmpleadoService.java
package com.gv.mx.empleados.application;

import com.gv.mx.empleados.domain.Empleado;
import com.gv.mx.empleados.dto.EmpleadoDTO;
import com.gv.mx.empleados.infrastructure.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository repo;

    @Transactional(readOnly = true)
    public Page<EmpleadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public EmpleadoDTO obtener(Long id) {
        Empleado e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        return toDTO(e);
    }

    public EmpleadoDTO crear(EmpleadoDTO dto) {
        Empleado e = fromDTO(new Empleado(), dto);
        e = repo.save(e);
        return toDTO(e);
    }

    public EmpleadoDTO actualizar(Long id, EmpleadoDTO dto) {
        Empleado e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        e = fromDTO(e, dto);
        e = repo.save(e);
        return toDTO(e);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
        }
        repo.deleteById(id);
    }

    // ====== Mapeos ======
    private EmpleadoDTO toDTO(Empleado e) {
        return new EmpleadoDTO(
                e.getId(),
                e.getNumEmpleado(),
                e.getNombres(),
                e.getApellidoPaterno(),
                e.getApellidoMaterno(),
                e.getEmail(),
                e.getActivo()
        );
    }

    private Empleado fromDTO(Empleado e, EmpleadoDTO dto) {
        // Solo campos base usados por el DTO record simple
        e.setNumEmpleado(dto.numEmpleado());
        e.setNombres(dto.nombres());
        e.setApellidoPaterno(dto.apellidoPaterno());
        e.setApellidoMaterno(dto.apellidoMaterno());
        e.setEmail(dto.email());
        if (dto.activo() != null) e.setActivo(dto.activo());
        return e;
    }
}

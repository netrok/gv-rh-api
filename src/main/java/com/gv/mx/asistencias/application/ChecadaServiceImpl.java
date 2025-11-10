// src/main/java/com/gv/mx/asistencias/application/impl/ChecadaServiceImpl.java
package com.gv.mx.asistencias.application.impl;

import com.gv.mx.asistencias.application.ChecadaFilter;
import com.gv.mx.asistencias.application.ChecadaService;
import com.gv.mx.asistencias.application.ChecadaSpecs;
import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.domain.ChecadaTipo;
import com.gv.mx.asistencias.dto.ChecadaDTO;
import com.gv.mx.asistencias.mapper.AsistenciasMapper;
import com.gv.mx.asistencias.repo.ChecadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.gv.mx.shared.utils.Pageables.safe;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecadaServiceImpl implements ChecadaService {

    private final ChecadaRepository repo;
    private final AsistenciasMapper mapper;

    // Usa EXACTAMENTE los nombres de tu entidad Checada
    private static final Set<String> ALLOWED_SORT = Set.of(
            "id", "empleadoId", "tipo", "fechaHora", "dispositivo", "createdAt"
    );

    @Override
    @Transactional(readOnly = true)
    public Page<ChecadaDTO> listar(ChecadaFilter f, Pageable pageable) {
        Pageable p = safe(pageable, ALLOWED_SORT, "fechaHora");
        // El filtrado se hace en ChecadaSpecs (NO aquí)
        return repo.findAll(ChecadaSpecs.from(f), p).map(mapper::toDto);
    }

    @Override
    public ChecadaDTO crear(ChecadaDTO dto) {
        Checada c = new Checada();
        c.setId(null);
        c.setEmpleadoId(dto.empleadoId());
        c.setFechaHora(dto.fechaHora());
        c.setDispositivo(dto.dispositivo());
        c.setUbicacion(dto.ubicacion());

        // Lee dto.tipo() o dto.tipoChecada(), normaliza a ENUM
        String tipoIn = readTipo(dto);
        if (tipoIn != null && !tipoIn.isBlank()) {
            ChecadaTipo tipoEnum;
            try {
                tipoEnum = ChecadaTipo.valueOf(tipoIn.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Tipo de checada inválido: " + tipoIn + " (usa valores de ChecadaTipo, p.ej. ENT o SAL)"
                );
            }
            c.setTipo(tipoEnum);
        }

        Checada saved = repo.save(c);
        return mapper.toDto(saved);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- Helper: soporta records con 'tipo' o 'tipoChecada'
    private static String readTipo(ChecadaDTO dto) {
        try {
            return (String) ChecadaDTO.class.getMethod("tipo").invoke(dto);
        } catch (ReflectiveOperationException e1) {
            try {
                return (String) ChecadaDTO.class.getMethod("tipoChecada").invoke(dto);
            } catch (ReflectiveOperationException e2) {
                return null;
            }
        }
    }
}

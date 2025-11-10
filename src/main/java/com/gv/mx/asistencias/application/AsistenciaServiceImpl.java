package com.gv.mx.asistencias.application.impl;

import com.gv.mx.asistencias.application.AsistenciaFilter;
import com.gv.mx.asistencias.application.AsistenciaService;
import com.gv.mx.asistencias.application.AsistenciaSpecs;
import com.gv.mx.asistencias.domain.Asistencia;
import com.gv.mx.asistencias.dto.AsistenciaDTO;
import com.gv.mx.asistencias.mapper.AsistenciasMapper;
import com.gv.mx.asistencias.repo.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.gv.mx.shared.utils.Pageables.safe;

@Service
@RequiredArgsConstructor
@Transactional
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository repo;
    private final AsistenciasMapper mapper;

    // Ajusta estos nombres a tu Entity real:
    private static final Set<String> ALLOWED_SORT = Set.of(
            "id",
            "empleado.id",   // si tu entity tiene ManyToOne Empleado empleado; si es Long empleadoId -> "empleadoId"
            "fecha",
            "estado",
            "createdAt"
    );

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaDTO> listar(AsistenciaFilter filtro, Pageable pageable) {
        var p = safe(pageable, ALLOWED_SORT, "fecha");
        var spec = AsistenciaSpecs.conFiltros(filtro);
        Page<Asistencia> page = repo.findAll(spec, p);
        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaDTO porDia(Long empleadoId, String yyyyMMdd) {
        LocalDate fecha = LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
        Specification<Asistencia> s = (root, q, cb) -> cb.and(
                // Si tu campo es empleadoId (Long), cambia a root.get("empleadoId")
                cb.equal(root.get("empleado").get("id"), empleadoId),
                cb.equal(root.get("fecha"), fecha)
        );

        Asistencia a = repo.findOne(s)
                .orElseThrow(() -> new NoSuchElementException("No existe asistencia para empleado=" + empleadoId + " en " + fecha));
        return mapper.toDto(a);
    }

    @Override
    public int recalcular(AsistenciaFilter filtro) {
        // Placeholder: aquí iría tu lógica real de recálculo.
        // De momento, solo contamos cuántos registros entrarían al recálculo.
        var spec = AsistenciaSpecs.conFiltros(filtro);
        return (int) repo.count(spec);
    }
}

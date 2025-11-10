package com.gv.mx.asistencias.application.impl;

import com.gv.mx.asistencias.application.AsistenciaFilter;
import com.gv.mx.asistencias.application.AsistenciaService;
import com.gv.mx.asistencias.application.AsistenciaSpecs;
import com.gv.mx.asistencias.domain.Asistencia;
import com.gv.mx.asistencias.domain.AsistenciaEstado;
import com.gv.mx.asistencias.domain.Checada;
import com.gv.mx.asistencias.domain.ChecadaTipo;
import com.gv.mx.asistencias.repo.AsistenciaRepository;
import com.gv.mx.asistencias.repo.ChecadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.gv.mx.shared.utils.Pageables.safe;

@Service
@RequiredArgsConstructor
@Transactional
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistRepo;
    private final ChecadaRepository checadaRepo;

    private static final Set<String> ALLOWED_SORT = Set.of(
            "id","empleadoId","fecha","horaEntrada","horaSalida","estado","createdAt"
    );

    @Override
    @Transactional(readOnly = true)
    public Page<Asistencia> listar(AsistenciaFilter f, Pageable pageable) {
        Pageable p = safe(pageable, ALLOWED_SORT, "fecha");
        return asistRepo.findAll(AsistenciaSpecs.from(f), p);
    }

    @Override
    public Asistencia generarParaFecha(Long empleadoId, LocalDate fecha) {
        // Trae checadas del día
        Specification<Checada> spec = (root, q, cb) -> cb.and(
                cb.equal(root.get("empleadoId"), empleadoId),
                cb.between(root.get("fechaHora"),
                        fecha.atStartOfDay(),
                        fecha.plusDays(1).atStartOfDay().minusNanos(1))
        );
        List<Checada> checadas = checadaRepo.findAll(spec);
        checadas.sort(Comparator.comparing(Checada::getFechaHora));

        LocalTime ent = null;
        LocalTime sal = null;

        // Simple: primera ENT como entrada, última SAL como salida
        for (Checada c : checadas) {
            if (c.getTipo() == ChecadaTipo.ENT) {
                if (ent == null) ent = c.getFechaHora().toLocalTime();
            } else if (c.getTipo() == ChecadaTipo.SAL) {
                sal = c.getFechaHora().toLocalTime();
            }
        }

        Asistencia a = asistRepo.findOne((root, q, cb) -> cb.and(
                cb.equal(root.get("empleadoId"), empleadoId),
                cb.equal(root.get("fecha"), fecha)
        )).orElseGet(Asistencia::new);

        a.setEmpleadoId(empleadoId);
        a.setFecha(fecha);
        a.setHoraEntrada(ent);
        a.setHoraSalida(sal);

        // Estado básico
        if (ent == null && sal == null) {
            a.setEstado(AsistenciaEstado.FALTA);
        } else if (ent != null && sal == null) {
            a.setEstado(AsistenciaEstado.INCOMPLETA);
        } else {
            a.setEstado(AsistenciaEstado.NORMAL);
        }

        a.setOrigen("auto");
        return asistRepo.save(a);
    }
}

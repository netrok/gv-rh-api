// src/main/java/com/gv/mx/vacaciones/application/VacacionesServiceImpl.java
package com.gv.mx.vacaciones.application;

import com.gv.mx.calendario.domain.Feriado;
import com.gv.mx.calendario.infrastructure.FeriadoRepo;
import com.gv.mx.vacaciones.domain.PeriodoVacacional;
import com.gv.mx.vacaciones.domain.SaldoVacaciones;
import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import com.gv.mx.vacaciones.domain.VacacionEstado;
import com.gv.mx.vacaciones.dto.PeriodoDTO;
import com.gv.mx.vacaciones.dto.SaldoDTO;
import com.gv.mx.vacaciones.dto.SolicitudDTO;
import com.gv.mx.vacaciones.infrastructure.PeriodoRepository;
import com.gv.mx.vacaciones.infrastructure.SaldoRepository;
import com.gv.mx.vacaciones.infrastructure.SolicitudRepository;
import com.gv.mx.vacaciones.mapper.VacacionesMapper;
import com.gv.mx.vacaciones.web.dto.DisponibilidadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class VacacionesServiceImpl implements VacacionesService {

    private final PeriodoRepository periodoRepo;
    private final SaldoRepository saldoRepo;
    private final SolicitudRepository solRepo;
    private final FeriadoRepo feriadoRepo; // módulo calendario
    private final VacacionesMapper mapper;

    @Value("${app.vacaciones.incluir_feriados_extra:false}")
    private boolean incluirFeriadosExtra; // si es true, NO se excluyen del cómputo

    public VacacionesServiceImpl(PeriodoRepository periodoRepo,
                                 SaldoRepository saldoRepo,
                                 SolicitudRepository solRepo,
                                 FeriadoRepo feriadoRepo,
                                 VacacionesMapper mapper) {
        this.periodoRepo = periodoRepo;
        this.saldoRepo = saldoRepo;
        this.solRepo = solRepo;
        this.feriadoRepo = feriadoRepo;
        this.mapper = mapper;
    }

    // ----- Periodo -----
    @Override
    @Transactional
    public PeriodoDTO upsertPeriodo(PeriodoDTO dto) {
        var p = periodoRepo.findByAnio(dto.anio()).orElseGet(PeriodoVacacional::new);
        p.setAnio(dto.anio());
        p.setFechaInicio(dto.fechaInicio());
        p.setFechaFin(dto.fechaFin());
        return mapper.toDto(periodoRepo.save(p));
    }

    // ----- Saldo -----
    @Override
    @Transactional
    public SaldoDTO asignarSaldo(Long empleadoId, Integer anio, Integer diasAsignados) {
        var s = saldoRepo.findByEmpleadoIdAndAnio(empleadoId, anio).orElseGet(SaldoVacaciones::new);
        s.setEmpleadoId(empleadoId);
        s.setAnio(anio);
        s.setDiasAsignados(diasAsignados);
        int disfrutados = s.getDiasDisfrutados() != null ? s.getDiasDisfrutados() : 0;
        s.setDiasPendientes(Math.max(0, diasAsignados - disfrutados));
        return mapper.toDto(saldoRepo.save(s));
    }

    // ----- Solicitudes -----
    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudDTO> listarSolicitudes(Pageable pageable) {
        return solRepo.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public SolicitudDTO crearSolicitud(Long empleadoId, LocalDate desde, LocalDate hasta, String motivo, String adjunto) {
        if (hasta.isBefore(desde)) throw new IllegalArgumentException("Rango inválido");
        int anio = desde.getYear();

        var periodo = periodoRepo.findByAnio(anio)
                .orElseThrow(() -> new IllegalStateException("Periodo no configurado"));
        if (desde.isBefore(periodo.getFechaInicio()) || hasta.isAfter(periodo.getFechaFin()))
            throw new IllegalArgumentException("Fuera del periodo");

        int diasHabiles = businessDays(desde, hasta);
        if (diasHabiles <= 0) throw new IllegalArgumentException("Sin días hábiles");

        var saldo = saldoRepo.findByEmpleadoIdAndAnio(empleadoId, anio)
                .orElseThrow(() -> new IllegalStateException("Saldo no asignado"));
        if (saldo.getDiasPendientes() < diasHabiles) throw new IllegalStateException("Saldo insuficiente");

        var s = new SolicitudVacaciones();
        s.setEmpleadoId(empleadoId);
        s.setAnio(anio);
        s.setFechaDesde(desde);
        s.setFechaHasta(hasta);
        s.setDias(diasHabiles);
        s.setMotivo(motivo);
        s.setAdjunto(adjunto);
        s.setEstado(VacacionEstado.PENDIENTE);
        return mapper.toDto(solRepo.save(s));
    }

    @Override
    @Transactional
    public SolicitudDTO aprobar(Long id) {
        var s = solRepo.findById(id).orElseThrow();
        if (s.getEstado() != VacacionEstado.PENDIENTE) return mapper.toDto(s);

        var saldo = saldoRepo.findByEmpleadoIdAndAnio(s.getEmpleadoId(), s.getAnio()).orElseThrow();
        if (saldo.getDiasPendientes() < s.getDias()) throw new IllegalStateException("Saldo insuficiente");

        saldo.setDiasDisfrutados((saldo.getDiasDisfrutados() == null ? 0 : saldo.getDiasDisfrutados()) + s.getDias());
        saldo.setDiasPendientes(Math.max(0, saldo.getDiasAsignados() - saldo.getDiasDisfrutados()));
        saldoRepo.save(saldo);

        s.setEstado(VacacionEstado.APROBADA);
        return mapper.toDto(solRepo.save(s));
    }

    @Override
    @Transactional
    public SolicitudDTO rechazar(Long id, String motivo) {
        var s = solRepo.findById(id).orElseThrow();
        s.setEstado(VacacionEstado.RECHAZADA);
        if (motivo != null && !motivo.isBlank()) s.setMotivo(motivo);
        return mapper.toDto(solRepo.save(s));
    }

    @Override
    @Transactional
    public SolicitudDTO cancelar(Long id) {
        var s = solRepo.findById(id).orElseThrow();
        if (s.getEstado() == VacacionEstado.APROBADA) {
            var saldo = saldoRepo.findByEmpleadoIdAndAnio(s.getEmpleadoId(), s.getAnio()).orElseThrow();
            int disfrutados = Math.max(0, (saldo.getDiasDisfrutados() == null ? 0 : saldo.getDiasDisfrutados()) - s.getDias());
            saldo.setDiasDisfrutados(disfrutados);
            saldo.setDiasPendientes(Math.max(0, saldo.getDiasAsignados() - disfrutados));
            saldoRepo.save(saldo);
        }
        s.setEstado(VacacionEstado.CANCELADA);
        return mapper.toDto(solRepo.save(s));
    }

    // ----- Disponibilidad (DTO del web layer) -----
    @Override
    @Transactional(readOnly = true)
    public DisponibilidadDTO disponibilidad(Long empleadoId, Integer anio) {
        var saldo = saldoRepo.findByEmpleadoIdAndAnio(empleadoId, anio).orElseThrow();
        int asignados   = saldo.getDiasAsignados() == null ? 0 : saldo.getDiasAsignados();
        int disfrutados = saldo.getDiasDisfrutados() == null ? 0 : saldo.getDiasDisfrutados();
        int pendientes  = Math.max(0, asignados - disfrutados);
        return new DisponibilidadDTO(empleadoId, anio, asignados, disfrutados, pendientes);
    }

    // ----- Listado para export (filtros simples por año/estado) -----
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudVacaciones> buscarSolicitudes(Integer anio, String estado) {
        var list = solRepo.findAll();
        if (anio != null)   list.removeIf(s -> !anio.equals(s.getAnio()));
        if (estado != null) list.removeIf(s -> !estado.equalsIgnoreCase(s.getEstado().name()));
        return list;
    }

    // ----- Utilidad: días hábiles L–V, excluye feriados si incluirFeriadosExtra=false -----
    private int businessDays(LocalDate start, LocalDate end) {
        var feriados = incluirFeriadosExtra
                ? java.util.Set.<LocalDate>of()
                : new HashSet<>(feriadoRepo.findByFechaBetween(start, end)
                .stream().map(Feriado::getFecha).toList());

        int days = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            DayOfWeek w = d.getDayOfWeek();
            boolean habil = (w != DayOfWeek.SATURDAY && w != DayOfWeek.SUNDAY);
            if (habil && !feriados.contains(d)) days++;
        }
        return days;
    }
}

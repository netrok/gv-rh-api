// VacacionesServiceImpl.java
package com.gv.mx.vacaciones.application;

import com.gv.mx.vacaciones.domain.*;
import com.gv.mx.vacaciones.dto.*;
import com.gv.mx.vacaciones.infrastructure.*;
import com.gv.mx.vacaciones.mapper.VacacionesMapper;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class VacacionesServiceImpl implements VacacionesService {

    private final PeriodoRepository periodoRepo;
    private final SaldoRepository saldoRepo;
    private final SolicitudRepository solRepo;
    private final VacacionesMapper mapper;

    public VacacionesServiceImpl(PeriodoRepository p, SaldoRepository s, SolicitudRepository sr, VacacionesMapper m){
        this.periodoRepo=p; this.saldoRepo=s; this.solRepo=sr; this.mapper=m;
    }

    @Override
    public PeriodoDTO upsertPeriodo(PeriodoDTO dto){
        var p = periodoRepo.findByAnio(dto.anio()).orElseGet(PeriodoVacacional::new);
        p.setAnio(dto.anio()); p.setFechaInicio(dto.fechaInicio()); p.setFechaFin(dto.fechaFin());
        return mapper.toDto(periodoRepo.save(p));
    }

    @Override
    public SaldoDTO asignarSaldo(Long empleadoId, Integer anio, Integer diasAsignados){
        var s = saldoRepo.findByEmpleadoIdAndAnio(empleadoId, anio).orElseGet(SaldoVacaciones::new);
        s.setEmpleadoId(empleadoId); s.setAnio(anio);
        s.setDiasAsignados(diasAsignados);
        // recalcula pendientes
        int disfrutados = s.getDiasDisfrutados()!=null? s.getDiasDisfrutados():0;
        s.setDiasPendientes(Math.max(0, diasAsignados - disfrutados));
        return mapper.toDto(saldoRepo.save(s));
    }

    @Override
    public Page<SolicitudDTO> listarSolicitudes(Pageable p){ return solRepo.findAll(p).map(mapper::toDto); }

    @Override @Transactional
    public SolicitudDTO crearSolicitud(Long empleadoId, LocalDate desde, LocalDate hasta, String motivo, String adjunto){
        if (hasta.isBefore(desde)) throw new IllegalArgumentException("Rango inválido");
        int anio = desde.getYear();
        var periodo = periodoRepo.findByAnio(anio).orElseThrow(() -> new IllegalStateException("Periodo no configurado"));
        if (desde.isBefore(periodo.getFechaInicio()) || hasta.isAfter(periodo.getFechaFin()))
            throw new IllegalArgumentException("Fuera del periodo");

        int diasHabiles = businessDays(desde, hasta);
        var saldo = saldoRepo.findByEmpleadoIdAndAnio(empleadoId, anio)
                .orElseThrow(() -> new IllegalStateException("Saldo no asignado"));

        if (diasHabiles <= 0) throw new IllegalArgumentException("Sin días hábiles");
        if (saldo.getDiasPendientes() < diasHabiles) throw new IllegalStateException("Saldo insuficiente");

        var s = new SolicitudVacaciones();
        s.setEmpleadoId(empleadoId); s.setAnio(anio);
        s.setFechaDesde(desde); s.setFechaHasta(hasta);
        s.setDias(diasHabiles); s.setMotivo(motivo); s.setAdjunto(adjunto);
        s.setEstado(VacacionEstado.PENDIENTE);
        return mapper.toDto(solRepo.save(s));
    }

    @Override @Transactional
    public SolicitudDTO aprobar(Long id){
        var s = solRepo.findById(id).orElseThrow();
        if (s.getEstado()!=VacacionEstado.PENDIENTE) return mapper.toDto(s);
        var saldo = saldoRepo.findByEmpleadoIdAndAnio(s.getEmpleadoId(), s.getAnio()).orElseThrow();
        if (saldo.getDiasPendientes() < s.getDias()) throw new IllegalStateException("Saldo insuficiente");
        saldo.setDiasDisfrutados(saldo.getDiasDisfrutados() + s.getDias());
        saldo.setDiasPendientes(Math.max(0, saldo.getDiasAsignados() - saldo.getDiasDisfrutados()));
        saldoRepo.save(saldo);
        s.setEstado(VacacionEstado.APROBADA);
        return mapper.toDto(solRepo.save(s));
    }

    @Override @Transactional
    public SolicitudDTO rechazar(Long id, String motivo){
        var s = solRepo.findById(id).orElseThrow();
        s.setEstado(VacacionEstado.RECHAZADA);
        if (motivo!=null && !motivo.isBlank()) s.setMotivo(motivo);
        return mapper.toDto(solRepo.save(s));
    }

    @Override @Transactional
    public SolicitudDTO cancelar(Long id){
        var s = solRepo.findById(id).orElseThrow();
        if (s.getEstado()==VacacionEstado.APROBADA) {
            var saldo = saldoRepo.findByEmpleadoIdAndAnio(s.getEmpleadoId(), s.getAnio()).orElseThrow();
            saldo.setDiasDisfrutados(Math.max(0, saldo.getDiasDisfrutados() - s.getDias()));
            saldo.setDiasPendientes(Math.max(0, saldo.getDiasAsignados() - saldo.getDiasDisfrutados()));
            saldoRepo.save(saldo);
        }
        s.setEstado(VacacionEstado.CANCELADA);
        return mapper.toDto(solRepo.save(s));
    }

    // L-V (sin feriados; luego añadimos tabla de feriados)
    private int businessDays(LocalDate start, LocalDate end){
        int days=0;
        for(LocalDate d=start; !d.isAfter(end); d=d.plusDays(1)){
            DayOfWeek w = d.getDayOfWeek();
            if (w!=DayOfWeek.SATURDAY && w!=DayOfWeek.SUNDAY) days++;
        }
        return days;
    }
}

// SolicitudVacaciones.java
package com.gv.mx.vacaciones.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity @Table(name="solicitudes_vacaciones")
public class SolicitudVacaciones {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="empleado_id", nullable=false) private Long empleadoId;
    @Column(nullable=false) private Integer anio;
    @Column(name="fecha_desde", nullable=false) private LocalDate fechaDesde;
    @Column(name="fecha_hasta", nullable=false) private LocalDate fechaHasta;
    @Column(nullable=false) private Integer dias;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private VacacionEstado estado = VacacionEstado.PENDIENTE;
    @Column(length=500) private String motivo;
    @Column(length=255) private String adjunto;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }
    // getters/setters
    // ...
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getEmpleadoId(){return empleadoId;} public void setEmpleadoId(Long v){this.empleadoId=v;}
    public Integer getAnio(){return anio;} public void setAnio(Integer v){this.anio=v;}
    public LocalDate getFechaDesde(){return fechaDesde;} public void setFechaDesde(LocalDate v){this.fechaDesde=v;}
    public LocalDate getFechaHasta(){return fechaHasta;} public void setFechaHasta(LocalDate v){this.fechaHasta=v;}
    public Integer getDias(){return dias;} public void setDias(Integer v){this.dias=v;}
    public VacacionEstado getEstado(){return estado;} public void setEstado(VacacionEstado v){this.estado=v;}
    public String getMotivo(){return motivo;} public void setMotivo(String v){this.motivo=v;}
    public String getAdjunto(){return adjunto;} public void setAdjunto(String v){this.adjunto=v;}
}

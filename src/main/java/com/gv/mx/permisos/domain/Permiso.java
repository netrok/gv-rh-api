package com.gv.mx.permisos.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "permisos")
public class Permiso {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PermisoTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PermisoEstado estado = PermisoEstado.PENDIENTE;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta", nullable = false)
    private LocalDate fechaHasta;

    @Column(precision = 5, scale = 2)
    private BigDecimal horas; // opcional

    @Column(length = 500)
    private String motivo;

    @Column(length = 255)
    private String adjunto;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public PermisoTipo getTipo() { return tipo; }
    public void setTipo(PermisoTipo tipo) { this.tipo = tipo; }
    public PermisoEstado getEstado() { return estado; }
    public void setEstado(PermisoEstado estado) { this.estado = estado; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
    public java.math.BigDecimal getHoras() { return horas; }
    public void setHoras(java.math.BigDecimal horas) { this.horas = horas; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getAdjunto() { return adjunto; }
    public void setAdjunto(String adjunto) { this.adjunto = adjunto; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}

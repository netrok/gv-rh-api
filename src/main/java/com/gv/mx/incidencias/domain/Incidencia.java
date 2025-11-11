// Incidencia.java
package com.gv.mx.incidencias.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity @Table(name="incidencias")
public class Incidencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="empleado_id", nullable=false) private Long empleadoId;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private IncidenciaTipo tipo;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private IncidenciaEstado estado = IncidenciaEstado.PENDIENTE;

    @Column(nullable=false) private LocalDate fecha;
    @Column private Integer minutos;              // para retardos/salidas
    @Column(length=500) private String motivo;
    @Column(length=255) private String adjunto;

    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }

    // getters/setters
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getEmpleadoId(){return empleadoId;} public void setEmpleadoId(Long v){this.empleadoId=v;}
    public IncidenciaTipo getTipo(){return tipo;} public void setTipo(IncidenciaTipo v){this.tipo=v;}
    public IncidenciaEstado getEstado(){return estado;} public void setEstado(IncidenciaEstado v){this.estado=v;}
    public LocalDate getFecha(){return fecha;} public void setFecha(LocalDate v){this.fecha=v;}
    public Integer getMinutos(){return minutos;} public void setMinutos(Integer v){this.minutos=v;}
    public String getMotivo(){return motivo;} public void setMotivo(String v){this.motivo=v;}
    public String getAdjunto(){return adjunto;} public void setAdjunto(String v){this.adjunto=v;}
    public OffsetDateTime getCreatedAt(){return createdAt;}
    public OffsetDateTime getUpdatedAt(){return updatedAt;}
}

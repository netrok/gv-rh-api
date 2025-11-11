// PeriodoVacacional.java
package com.gv.mx.vacaciones.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity @Table(name="periodos_vacacionales")
public class PeriodoVacacional {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private Integer anio;
    @Column(name="fecha_inicio", nullable=false) private LocalDate fechaInicio;
    @Column(name="fecha_fin",    nullable=false) private LocalDate fechaFin;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
    // getters/setters
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Integer getAnio(){return anio;} public void setAnio(Integer v){this.anio=v;}
    public LocalDate getFechaInicio(){return fechaInicio;} public void setFechaInicio(LocalDate v){this.fechaInicio=v;}
    public LocalDate getFechaFin(){return fechaFin;} public void setFechaFin(LocalDate v){this.fechaFin=v;}
}

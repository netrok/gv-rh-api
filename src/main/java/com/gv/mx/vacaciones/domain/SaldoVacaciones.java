// SaldoVacaciones.java
package com.gv.mx.vacaciones.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity @Table(name="saldos_vacaciones",
        uniqueConstraints=@UniqueConstraint(columnNames={"empleado_id","anio"}))
public class SaldoVacaciones {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="empleado_id", nullable=false) private Long empleadoId;
    @Column(nullable=false) private Integer anio;
    @Column(name="dias_asignados",   nullable=false) private Integer diasAsignados = 0;
    @Column(name="dias_disfrutados", nullable=false) private Integer diasDisfrutados = 0;
    @Column(name="dias_pendientes",  nullable=false) private Integer diasPendientes = 0;
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
    @PreUpdate void onUpdate(){ this.updatedAt = OffsetDateTime.now(); }
    // getters/setters
    // ...
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getEmpleadoId(){return empleadoId;} public void setEmpleadoId(Long v){this.empleadoId=v;}
    public Integer getAnio(){return anio;} public void setAnio(Integer v){this.anio=v;}
    public Integer getDiasAsignados(){return diasAsignados;} public void setDiasAsignados(Integer v){this.diasAsignados=v;}
    public Integer getDiasDisfrutados(){return diasDisfrutados;} public void setDiasDisfrutados(Integer v){this.diasDisfrutados=v;}
    public Integer getDiasPendientes(){return diasPendientes;} public void setDiasPendientes(Integer v){this.diasPendientes=v;}
}

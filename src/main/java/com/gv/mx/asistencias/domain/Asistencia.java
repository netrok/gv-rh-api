package com.gv.mx.asistencias.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "asist_asistencias",
        uniqueConstraints = @UniqueConstraint(name = "uc_asistencia_emp_fecha", columnNames = {"empleado_id","fecha"}),
        indexes = {
                @Index(name = "ix_asistencia_empleado", columnList = "empleado_id"),
                @Index(name = "ix_asistencia_fecha", columnList = "fecha"),
                @Index(name = "ix_asistencia_estado", columnList = "estado")
        })
public class Asistencia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "retardo_min")
    private Integer retardoMin;

    @Column(name = "salida_anticipada_min")
    private Integer salidaAnticipadaMin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private AsistenciaEstado estado;

    @Column(length = 30)
    private String origen; // "auto" | "manual"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (estado == null) estado = AsistenciaEstado.NORMAL;
        if (origen == null) origen = "manual";
    }
}

package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "cat_horarios",
        indexes = {
                @Index(name = "ix_horario_nombre", columnList = "nombre"),
                @Index(name = "ix_horario_activo", columnList = "activo")
        })
public class Horario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @NotNull
    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @NotNull
    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    // Wrapper para permitir null-checks en el service
    @Min(0) @Max(600)
    @Column(name = "minutos_comida", nullable = false)
    private Integer minutosComida;

    // Wrappers (no primitivos) para permitir null-checks en el service
    @Column(nullable = false) private Boolean lunes;
    @Column(nullable = false) private Boolean martes;
    @Column(nullable = false) private Boolean miercoles;
    @Column(nullable = false) private Boolean jueves;
    @Column(nullable = false) private Boolean viernes;
    @Column(nullable = false) private Boolean sabado;
    @Column(nullable = false) private Boolean domingo;

    @Column(nullable = false)
    private Boolean activo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (minutosComida == null) minutosComida = 0;

        if (lunes     == null) lunes     = Boolean.TRUE;
        if (martes    == null) martes    = Boolean.TRUE;
        if (miercoles == null) miercoles = Boolean.TRUE;
        if (jueves    == null) jueves    = Boolean.TRUE;
        if (viernes   == null) viernes   = Boolean.TRUE;
        if (sabado    == null) sabado    = Boolean.FALSE;
        if (domingo   == null) domingo   = Boolean.FALSE;

        if (activo    == null) activo    = Boolean.TRUE;
    }
}
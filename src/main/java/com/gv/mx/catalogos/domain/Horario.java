package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "cat_horarios",
        indexes = {
                @Index(name = "ix_horario_nombre", columnList = "nombre"),
                @Index(name = "ix_horario_activo", columnList = "activo")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
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

    @NotNull @Min(0) @Max(600)
    @Column(name = "minutos_comida", nullable = false)
    private Integer minutosComida = 0;

    @Column(nullable = false) private Boolean lunes     = true;
    @Column(nullable = false) private Boolean martes    = true;
    @Column(nullable = false) private Boolean miercoles = true;
    @Column(nullable = false) private Boolean jueves    = true;
    @Column(nullable = false) private Boolean viernes   = true;
    @Column(nullable = false) private Boolean sabado    = false;
    @Column(nullable = false) private Boolean domingo   = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
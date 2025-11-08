package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "cat_turnos",
        indexes = {
                @Index(name = "ix_turno_nombre", columnList = "nombre"),
                @Index(name = "ix_turno_activo", columnList = "activo")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Turno {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @NotNull
    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @NotNull
    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @NotNull @Min(0) @Max(600)
    @Column(name = "tolerancia_entrada_min", nullable = false)
    private Integer toleranciaEntradaMin = 0;

    @NotNull @Min(0) @Max(600)
    @Column(name = "tolerancia_salida_min", nullable = false)
    private Integer toleranciaSalidaMin = 0;

    @NotNull @Min(0) @Max(1440)
    @Column(name = "ventana_inicio_min", nullable = false)
    private Integer ventanaInicioMin = 180;

    @NotNull @Min(0) @Max(1440)
    @Column(name = "ventana_fin_min", nullable = false)
    private Integer ventanaFinMin = 180;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
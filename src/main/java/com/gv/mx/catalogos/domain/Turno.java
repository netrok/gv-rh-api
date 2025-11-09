// src/main/java/com/gv/mx/catalogos/domain/Turno.java
package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "cat_turnos",
        indexes = {
                @Index(name = "ix_turno_nombre", columnList = "nombre"),
                @Index(name = "ix_turno_activo", columnList = "activo")
        })
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @NotNull
    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @NotNull
    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @NotNull
    @Column(name = "tolerancia_entrada_min", nullable = false)
    private Integer toleranciaEntradaMin = 0;

    @NotNull
    @Column(name = "tolerancia_salida_min", nullable = false)
    private Integer toleranciaSalidaMin = 0;

    @NotNull
    @Column(name = "ventana_inicio_min", nullable = false)
    private Integer ventanaInicioMin = 180;

    @NotNull
    @Column(name = "ventana_fin_min", nullable = false)
    private Integer ventanaFinMin = 180;

    @NotNull
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Builder solo con los campos que debe setear el cliente
    @Builder
    public Turno(String nombre, LocalTime horaEntrada, LocalTime horaSalida) {
        this.nombre = nombre;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
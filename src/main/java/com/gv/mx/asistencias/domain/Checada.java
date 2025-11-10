package com.gv.mx.asistencias.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "asist_checadas",
        indexes = {
                @Index(name = "ix_checada_empleado", columnList = "empleado_id"),
                @Index(name = "ix_checada_fecha", columnList = "fecha_hora"),
                @Index(name = "ix_checada_tipo", columnList = "tipo")
        })
public class Checada {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private ChecadaTipo tipo; // ENT | SAL

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 80)
    private String dispositivo;

    @Column(length = 120)
    private String ubicacion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

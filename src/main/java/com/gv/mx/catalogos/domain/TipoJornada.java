package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "cat_tipos_jornada",
        indexes = {
                @Index(name = "ix_tipo_jornada_nombre", columnList = "nombre"),
                @Index(name = "ix_tipo_jornada_activo", columnList = "activo")
        })
public class TipoJornada {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    // Wrapper para poder hacer null-check en services
    @Column(nullable = false)
    private Boolean activo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (activo == null) activo = Boolean.TRUE;
    }
}
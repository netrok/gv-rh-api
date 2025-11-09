package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "cat_motivos_baja")
public class MotivoBaja {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=120)
    private String nombre;

    @Column(nullable=false)
    private Boolean activo; // wrapper

    @Column(name = "created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (activo == null)   activo = Boolean.TRUE;
        if (createdAt == null) createdAt = Instant.now();
    }
}
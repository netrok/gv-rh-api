package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "cat_tipos_contrato",
        uniqueConstraints = @UniqueConstraint(name = "uk_tipo_contrato_nombre", columnNames = "nombre"),
        indexes = {
                @Index(name = "ix_tc_nombre", columnList = "nombre"),
                @Index(name = "ix_tc_activo", columnList = "activo")
        })
public class TipoContrato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo; // <-- wrapper, para permitir null en updates parciales

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (activo == null) activo = Boolean.TRUE;
    }
}
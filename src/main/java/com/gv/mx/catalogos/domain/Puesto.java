package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(exclude = "departamento")
@Entity
@Table(
        name = "cat_puestos",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_puesto_dep_nombre",
                columnNames = {"departamento_id", "nombre"}
        ),
        indexes = {
                @Index(name = "ix_pue_dep",    columnList = "departamento_id"),
                @Index(name = "ix_pue_nombre", columnList = "nombre"),
                @Index(name = "ix_pue_activo", columnList = "activo")
        }
)
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "departamento_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_puesto_departamento")
    )
    private Departamento departamento;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo; // wrapper para permitir null-check en services

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (activo == null) activo = Boolean.TRUE;
    }
}
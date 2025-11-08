package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "cat_puestos",
        uniqueConstraints = @UniqueConstraint(name = "uc_puesto_dep_nombre", columnNames = {"departamento_id", "nombre"}),
        indexes = {
                @Index(name = "ix_pue_dep",    columnList = "departamento_id"),
                @Index(name = "ix_pue_nombre", columnList = "nombre"),
                @Index(name = "ix_pue_activo", columnList = "activo")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Puesto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_puesto_departamento"))
    private Departamento departamento;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
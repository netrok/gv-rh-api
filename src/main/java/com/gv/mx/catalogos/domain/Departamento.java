package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "cat_departamentos",
        indexes = {
                @Index(name = "ix_dep_nombre", columnList = "nombre"),
                @Index(name = "ix_dep_activo",  columnList = "activo")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Departamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
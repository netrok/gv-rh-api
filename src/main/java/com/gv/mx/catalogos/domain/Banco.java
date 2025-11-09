package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "cat_bancos",
        uniqueConstraints = @UniqueConstraint(name = "uk_banco_nombre", columnNames = "nombre"),
        indexes = {
                @Index(name = "ix_banco_nombre", columnList = "nombre"),
                @Index(name = "ix_banco_activo", columnList = "activo")
        })
public class Banco {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true; // Wrapper para poder defaultear cuando venga null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    public Banco(String nombre, Boolean activo) {
        this.nombre = nombre;
        this.activo = (activo == null) ? Boolean.TRUE : activo;
    }
}
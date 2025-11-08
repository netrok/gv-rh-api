package com.gv.mx.catalogos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "cat_bancos",
        uniqueConstraints = @UniqueConstraint(name = "uk_banco_nombre", columnNames = "nombre"),
        indexes = {
                @Index(name = "ix_banco_nombre", columnList = "nombre"),
                @Index(name = "ix_banco_activo", columnList = "activo")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
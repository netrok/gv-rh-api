package com.gv.mx.core.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=40)
    private String name; // ADMIN, RRHH

    @Column(name="created_at", nullable=false)
    private Instant createdAt = Instant.now();

    public Role() {}
    public Role(String name) { this.name = name; }

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

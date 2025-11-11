package com.gv.mx.calendario.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "feriado", uniqueConstraints = @UniqueConstraint(columnNames = "fecha"))
public class Feriado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 160)
    private String descripcion;

    @Column(nullable = false)
    private boolean nacional = true;

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isNacional() { return nacional; }
    public void setNacional(boolean nacional) { this.nacional = nacional; }
}

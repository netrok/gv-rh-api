package com.gv.mx.workflow.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_flow", indexes = @Index(name = "ix_flow_modulo_entidad", columnList = "modulo,entidadId"))
public class ApprovalFlow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String modulo;          // "vacaciones","permisos","incidencias"

    @Column(nullable = false, length = 80)
    private String entidadId;       // ID de la solicitud (string para flexibilidad)

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden asc")
    private List<ApprovalStep> steps = new ArrayList<>();

    // getters/setters
    public Long getId() { return id; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getEntidadId() { return entidadId; }
    public void setEntidadId(String entidadId) { this.entidadId = entidadId; }
    public List<ApprovalStep> getSteps() { return steps; }
}

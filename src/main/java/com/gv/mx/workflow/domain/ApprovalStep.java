package com.gv.mx.workflow.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "approval_step")
public class ApprovalStep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "flow_id", nullable = false)
    private ApprovalFlow flow;

    private Integer orden;
    private String aprobador; // username o id de usuario

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.PENDIENTE;

    private OffsetDateTime fechaDecision;
    private String comentario;

    public enum Estado { PENDIENTE, APROBADO, RECHAZADO }

    // getters/setters
    public Long getId() { return id; }
    public ApprovalFlow getFlow() { return flow; }
    public void setFlow(ApprovalFlow flow) { this.flow = flow; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public String getAprobador() { return aprobador; }
    public void setAprobador(String aprobador) { this.aprobador = aprobador; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public OffsetDateTime getFechaDecision() { return fechaDecision; }
    public void setFechaDecision(OffsetDateTime fechaDecision) { this.fechaDecision = fechaDecision; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}

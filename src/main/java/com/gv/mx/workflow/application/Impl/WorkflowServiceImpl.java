package com.gv.mx.workflow.application.impl;

import com.gv.mx.workflow.application.WorkflowService;
import com.gv.mx.workflow.domain.ApprovalFlow;
import com.gv.mx.workflow.domain.ApprovalStep;
import com.gv.mx.workflow.infrastructure.ApprovalFlowRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final ApprovalFlowRepo flowRepo;

    public WorkflowServiceImpl(ApprovalFlowRepo flowRepo) {
        this.flowRepo = flowRepo;
    }

    public void aprobar(String modulo, String entidadId, String comentario) {
        ApprovalFlow flow = flowRepo.findByModuloAndEntidadId(modulo, entidadId).orElseThrow();
        var step = flow.getSteps().stream()
                .filter(s -> s.getEstado() == ApprovalStep.Estado.PENDIENTE)
                .findFirst().orElseThrow();

        step.setEstado(ApprovalStep.Estado.APROBADO);
        step.setComentario(comentario);
        step.setFechaDecision(OffsetDateTime.now());

        // TODO: si fue el último, emitir evento de "APROBADO"
    }

    public void rechazar(String modulo, String entidadId, String comentario) {
        ApprovalFlow flow = flowRepo.findByModuloAndEntidadId(modulo, entidadId).orElseThrow();
        var step = flow.getSteps().stream()
                .filter(s -> s.getEstado() == ApprovalStep.Estado.PENDIENTE)
                .findFirst().orElseThrow();

        step.setEstado(ApprovalStep.Estado.RECHAZADO);
        step.setComentario(comentario);
        step.setFechaDecision(OffsetDateTime.now());

        // TODO: emitir evento de "RECHAZADO"
    }
}

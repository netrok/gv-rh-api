package com.gv.mx.workflow.application;

public interface WorkflowService {
    void aprobar(String modulo, String entidadId, String comentario);
    void rechazar(String modulo, String entidadId, String comentario);
}

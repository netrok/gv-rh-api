package com.gv.mx.workflow.web;

import com.gv.mx.workflow.application.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@Tag(name = "Workflow")
public class WorkflowController {

    private final WorkflowService service;
    public WorkflowController(WorkflowService service) { this.service = service; }

    @PostMapping("/{modulo}/{id}/aprobar")
    @Operation(summary = "Aprueba la solicitud actual del flujo")
    public void aprobar(@PathVariable String modulo, @PathVariable("id") String entidadId,
                        @RequestParam(required = false) String comentario) {
        service.aprobar(modulo, entidadId, comentario);
    }

    @PostMapping("/{modulo}/{id}/rechazar")
    @Operation(summary = "Rechaza la solicitud actual del flujo")
    public void rechazar(@PathVariable String modulo, @PathVariable("id") String entidadId,
                         @RequestParam(required = false) String comentario) {
        service.rechazar(modulo, entidadId, comentario);
    }
}

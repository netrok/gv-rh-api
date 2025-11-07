// src/main/java/com/gv/mx/empleados/web/EmpleadoExportController.java
package com.gv.mx.empleados.web;

import com.gv.mx.empleados.application.EmpleadoExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoExportController {

    private final EmpleadoExportService exportService;

    @GetMapping("/export")
    @Operation(summary = "Exporta empleados (XLSX o PDF) con filtros (q, departamentoId, puestoId, activo)")
    // Usa UNO de los dos, según tu SecurityConfig:
    // Si tu converter agrega ROLE_: usa hasAnyRole
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    // Si usas authorities planas sin ROLE_: usa hasAnyAuthority
    // @PreAuthorize("hasAnyAuthority('ADMIN','RRHH')")
    public ResponseEntity<byte[]> export(
            @Parameter(description = "Formato de salida (xlsx|pdf)")
            @RequestParam
            @Pattern(regexp = "xlsx|pdf", message = "format debe ser xlsx o pdf")
            String format,

            @Parameter(description = "Búsqueda libre en numEmpleado, nombre y email")
            @RequestParam(required = false) String q,

            @Parameter(description = "Filtro por departamento (id)")
            @RequestParam(required = false) Long departamentoId,

            @Parameter(description = "Filtro por puesto (id)")
            @RequestParam(required = false) Long puestoId,

            @Parameter(description = "Filtro por estado activo")
            @RequestParam(required = false) Boolean activo
    ) throws Exception {

        var rows = exportService.getRows(q, departamentoId, puestoId, activo);

        byte[] bytes;
        MediaType contentType;
        if ("xlsx".equalsIgnoreCase(format)) {
            bytes = exportService.toXlsx(rows);
            contentType = MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else { // pdf
            bytes = exportService.toPdf(rows);
            contentType = MediaType.APPLICATION_PDF;
        }

        // Nombre de archivo con timestamp y codificación RFC 5987
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String baseName = "empleados_" + timestamp + "." + format.toLowerCase();
        String encoded = URLEncoder.encode(baseName, StandardCharsets.UTF_8); // filename* para UTF-8
        String contentDisposition = "attachment; filename=\"" + baseName + "\"; filename*=UTF-8''" + encoded;

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .cacheControl(CacheControl.noCache()) // evita cache del navegador
                .body(bytes);
    }
}

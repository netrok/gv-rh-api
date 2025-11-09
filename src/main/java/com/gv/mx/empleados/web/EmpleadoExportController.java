package com.gv.mx.empleados.web;

import com.gv.mx.empleados.application.EmpleadoExportService;
import com.gv.mx.empleados.application.EmpleadoFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/empleados/export")
@RequiredArgsConstructor
public class EmpleadoExportController {

    private final EmpleadoExportService export;

    @GetMapping(
            value = "/xlsx",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Exporta empleados a XLSX (ADMIN/RRHH)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "XLSX generado",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    public ResponseEntity<byte[]> xlsx(
            @Parameter(description = "Búsqueda libre") @RequestParam(required = false) String q,
            @Parameter(description = "ID de Departamento") @RequestParam(required = false) Long departamentoId,
            @Parameter(description = "ID de Puesto") @RequestParam(required = false) Long puestoId,
            @Parameter(description = "Activo (true/false)") @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Fecha ingreso desde (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoDesde,
            @Parameter(description = "Fecha ingreso hasta (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoHasta
    ) {
        var f = new EmpleadoFilter(q, departamentoId, puestoId, activo, fechaIngresoDesde, fechaIngresoHasta);
        byte[] bytes = export.exportXlsx(f);

        String base = "empleados_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        String encoded = URLEncoder.encode(base, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + base + "\"; filename*=UTF-8''" + encoded)
                .cacheControl(CacheControl.noCache())
                .body(bytes);
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Exporta empleados a PDF (ADMIN/RRHH)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF generado",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    public ResponseEntity<byte[]> pdf(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoHasta
    ) {
        var f = new EmpleadoFilter(q, departamentoId, puestoId, activo, fechaIngresoDesde, fechaIngresoHasta);
        byte[] bytes = export.exportPdf(f);

        String base = "empleados_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        String encoded = URLEncoder.encode(base, StandardCharsets.UTF_8);

        // OJO: usa 'attachment' (no inline) para que Swagger UI ofrezca "Download file"
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + base + "\"; filename*=UTF-8''" + encoded)
                .cacheControl(CacheControl.noCache())
                .body(bytes);
    }
}
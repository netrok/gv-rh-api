// src/main/java/com/gv/mx/reportes/web/ReportesController.java
package com.gv.mx.reportes.web;

import com.gv.mx.reportes.application.JasperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes")
public class ReportesController {

    private final JasperService jasper;

    public ReportesController(JasperService jasper) {
        this.jasper = jasper;
    }

    @GetMapping("/ping")
    public String ping() {
        return "reportes-ok";
    }

    @GetMapping(value = "/empleados.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Demo PDF empleados (Jasper)")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH') or hasAuthority('SCOPE_reportes.read')")
    public ResponseEntity<byte[]> empleadosPdf(
            @RequestParam(required = false) @Size(max = 120) String departamento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) throws Exception {

        // TODO: reemplazar por consulta real (filtros: departamento, desde, hasta)
        record Row(String numEmpleado, String nombre, String depto, LocalDate ingreso) {}
        List<Row> rows = List.of(
                new Row("E001", "Juana Pérez", (departamento != null ? departamento : "Sistemas"), LocalDate.of(2023, 3, 10)),
                new Row("E002", "Carlos Ruiz", (departamento != null ? departamento : "Sistemas"), LocalDate.of(2024, 1, 5))
        );

        Map<String, Object> params = Map.of(
                "P_TITULO", "Listado de Empleados",
                "P_DEPARTAMENTO", (departamento != null ? departamento : "Todos"),
                "P_DESDE", (desde != null ? desde.toString() : ""),
                "P_HASTA", (hasta != null ? hasta.toString() : "")
        );

        byte[] pdf = jasper.renderPdfFromBeans("reports/empleados_simple.jrxml", params, rows);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=empleados.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}

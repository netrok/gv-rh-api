package com.gv.mx.reportes.web;

import com.gv.mx.reportes.application.JasperService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {
    private final JasperService jasper;
    public ReportesController(JasperService j){ this.jasper = j; }

    @PostMapping("/empleados/pdf")
    public ResponseEntity<byte[]> empleadosPdf(@RequestBody Map<String,Object> filtros){
        byte[] pdf = jasper.exportPdf("reports/empleados.jrxml", filtros);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=empleados.pdf")
                .body(pdf);
    }
}

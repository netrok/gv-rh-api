// src/main/java/com/gv/mx/empleados/application/impl/EmpleadoExportServiceImpl.java
package com.gv.mx.empleados.application.impl;

import com.gv.mx.empleados.application.EmpleadoExportService;
import com.gv.mx.empleados.application.EmpleadoFilter;
import com.gv.mx.empleados.application.export.EmpleadoPdfExporter;
import com.gv.mx.empleados.dto.ExportEmpleadoRow;
import com.gv.mx.empleados.infrastructure.EmpleadoRepository;
import com.gv.mx.empleados.infrastructure.projections.ExportEmpleadoProjectionWithNames;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpleadoExportServiceImpl implements EmpleadoExportService {

    private final EmpleadoRepository repo;

    @Override
    public byte[] exportXlsx(EmpleadoFilter f) {
        var rows = toRows(f);
        return toXlsx(rows);
    }

    @Override
    public byte[] exportPdf(EmpleadoFilter f) {
        var rows = toRows(f);
        try {
            var exporter = new EmpleadoPdfExporter(
                    rows,
                    EmpleadoPdfExporter.Options.builder()
                            .titulo("Listado de Empleados")
                            .empresa("Gran Vía")
                            .autor("GV RH")
                            .logoClasspath("static/logo.png") // opcional
                            .landscape(true)
                            .build()
            );
            return exporter.export();
        } catch (Exception ex) {
            throw new RuntimeException("No fue posible generar el PDF", ex);
        }
    }

    // ---- helpers ----
    private List<ExportEmpleadoRow> toRows(EmpleadoFilter f) {
        List<ExportEmpleadoProjectionWithNames> data =
                repo.findForExport(
                        f.q(),
                        f.departamentoId(),
                        f.puestoId(),
                        f.activo()
                );

        return data.stream()
                .map(p -> new ExportEmpleadoRow(
                        p.getId(),
                        p.getNumEmpleado(),
                        p.getNombres(),
                        p.getApellidoPaterno(),
                        p.getApellidoMaterno(),
                        p.getEmail(),
                        p.getDepartamentoNombre(),
                        p.getPuestoNombre(),
                        p.getFechaIngreso(),
                        p.getActivo()
                ))
                .toList();
    }

    private byte[] toXlsx(List<ExportEmpleadoRow> rows) {
        try (var wb = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("Empleados");

            // estilos
            CellStyle header = wb.createCellStyle();
            var fontH = wb.createFont();
            fontH.setBold(true);
            header.setFont(fontH);

            CellStyle date = wb.createCellStyle();
            short df = wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd");
            date.setDataFormat(df);

            // encabezados
            String[] cols = {"ID","NumEmpleado","Nombres","ApellidoP","ApellidoM","Email","Departamento","Puesto","FechaIngreso","Activo"};
            Row h = sh.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                var c = h.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(header);
            }

            // datos
            int r = 1;
            for (var x : rows) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(x.id());
                row.createCell(1).setCellValue(s(x.numEmpleado()));
                row.createCell(2).setCellValue(s(x.nombres()));
                row.createCell(3).setCellValue(s(x.apellidoPaterno()));
                row.createCell(4).setCellValue(s(x.apellidoMaterno()));
                row.createCell(5).setCellValue(s(x.email()));
                row.createCell(6).setCellValue(s(x.departamento()));
                row.createCell(7).setCellValue(s(x.puesto()));
                var cFecha = row.createCell(8);
                if (x.fechaIngreso() != null) {
                    cFecha.setCellValue(java.sql.Date.valueOf(x.fechaIngreso()));
                    cFecha.setCellStyle(date);
                }
                row.createCell(9).setCellValue(Boolean.TRUE.equals(x.activo()));
            }
            for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando XLSX", e);
        }
    }

    private static String s(String v) { return v == null ? "" : v; }
}

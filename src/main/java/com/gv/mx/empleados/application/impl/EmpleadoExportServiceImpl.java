package com.gv.mx.empleados.application.impl;

import com.gv.mx.empleados.application.EmpleadoExportService;
import com.gv.mx.empleados.application.EmpleadoFilter;
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

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
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
        return toPdf(rows);
    }

    // ---- helpers ----
    private List<ExportEmpleadoRow> toRows(EmpleadoFilter f) {
        List<ExportEmpleadoProjectionWithNames> data =
                repo.findForExport(
                        f.q(),              // asumiendo EmpleadoFilter es record: q(), departamentoId(), ...
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

    private byte[] toPdf(List<ExportEmpleadoRow> rows) {
        try (var out = new ByteArrayOutputStream()) {
            var doc = new Document(PageSize.LETTER.rotate(), 36, 36, 28, 28);
            var writer = PdfWriter.getInstance(doc, out);
            doc.addAuthor("GV RH");
            doc.addTitle("Empleados");
            doc.open();

            var title = new Paragraph("Listado de Empleados\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            doc.add(title);

            String[] cols = {"ID","NumEmpleado","Nombre","Apellidos","Email","Departamento","Puesto","FechaIngreso","Activo"};
            var table = new PdfPTable(cols.length);
            table.setWidthPercentage(100);

            // header
            for (var c : cols) {
                var cell = new PdfPCell(new Phrase(c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
                cell.setGrayFill(0.9f);
                table.addCell(cell);
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (var x : rows) {
                table.addCell(n(x.id()));
                table.addCell(s(x.numEmpleado()));
                table.addCell(s(x.nombres()));
                table.addCell((s(x.apellidoPaterno()) + " " + s(x.apellidoMaterno())).trim());
                table.addCell(s(x.email()));
                table.addCell(s(x.departamento()));
                table.addCell(s(x.puesto()));
                table.addCell(x.fechaIngreso() != null ? x.fechaIngreso().format(fmt) : "");
                table.addCell(Boolean.TRUE.equals(x.activo()) ? "Sí" : "No");
            }

            doc.add(table);
            doc.close();
            writer.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private static String s(String v) { return v == null ? "" : v; }
    private static String n(Object v) { return v == null ? "" : String.valueOf(v); }
}
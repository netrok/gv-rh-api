// src/main/java/com/gv/mx/vacaciones/application/export/SolicitudPdfExporter.java
package com.gv.mx.vacaciones.application.export;

import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;              // <-- ESTE IMPORT
import java.io.OutputStream;
import java.util.List;

public class SolicitudPdfExporter {
    public static void write(List<SolicitudVacaciones> rows, OutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Paragraph title = new Paragraph("Solicitudes de Vacaciones");
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12f);
        doc.add(title);

        String[] headers = {"ID","Empleado","Año","Desde","Hasta","Días","Estado","Motivo"};
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h));
            cell.setBackgroundColor(new Color(230, 230, 230)); // <-- ahora compila
            table.addCell(cell);
        }

        for (SolicitudVacaciones s : rows) {
            table.addCell(String.valueOf(s.getId()));
            table.addCell(String.valueOf(s.getEmpleadoId()));
            table.addCell(String.valueOf(s.getAnio()));
            table.addCell(s.getFechaDesde().toString());
            table.addCell(s.getFechaHasta().toString());
            table.addCell(String.valueOf(s.getDias()));
            table.addCell(s.getEstado().name());
            table.addCell(s.getMotivo() == null ? "" : s.getMotivo());
        }

        doc.add(table);
        doc.close();
    }
}

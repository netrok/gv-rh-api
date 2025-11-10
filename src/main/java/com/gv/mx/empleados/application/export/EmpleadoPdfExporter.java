// src/main/java/com/gv/mx/empleados/application/export/EmpleadoPdfExporter.java
package com.gv.mx.empleados.application.export;

import com.gv.mx.empleados.dto.ExportEmpleadoRow;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.awt.Color; // SOLO Color de AWT, nada de java.awt.*!
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class EmpleadoPdfExporter {

    @Builder
    public static class Options {
        public String titulo;          // "Listado de Empleados"
        public String logoClasspath;   // "static/logo.png" (opcional)
        public boolean landscape;      // true = apaisado
        public String autor;           // "GV RH"
        public String empresa;         // "Gran Vía"
    }

    private final List<ExportEmpleadoRow> rows;
    private final Options opts;

    public byte[] export() throws Exception {
        Rectangle pageSize = opts.landscape ? PageSize.LETTER.rotate() : PageSize.LETTER;
        Document doc = new Document(pageSize, 36, 36, 60, 36); // top=60 para header
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, out);

        // Metadatos
        doc.addAuthor(opts.autor != null ? opts.autor : "GV RH");
        doc.addCreator("GV RH API");
        doc.addTitle(opts.titulo != null ? opts.titulo : "Empleados");
        doc.addSubject("Export de empleados");
        doc.addCreationDate();

        // Header / Footer
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(opts);
        writer.setPageEvent(event);

        doc.open();

        // Respiro bajo header
        doc.add(new Paragraph(" "));

        PdfPTable table = buildTable();
        doc.add(table);

        doc.close();
        writer.close();
        return out.toByteArray();
    }

    private PdfPTable buildTable() throws Exception {
        String[] cols = {
                "ID","NumEmpleado","Nombre","Apellidos","Email",
                "Departamento","Puesto","FechaIngreso","Activo"
        };
        float[] widths = { 6f, 14f, 16f, 18f, 24f, 16f, 16f, 12f, 8f };

        PdfPTable table = new PdfPTable(cols.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setHeaderRows(1);

        // Estilos
        com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        com.lowagie.text.Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        // Header
        for (String c : cols) {
            PdfPCell th = new PdfPCell(new Phrase(c, headerFont));
            th.setHorizontalAlignment(Element.ALIGN_CENTER);
            th.setVerticalAlignment(Element.ALIGN_MIDDLE);
            th.setBackgroundColor(new Color(235, 238, 243)); // gris claro
            th.setPadding(6f);
            th.setBorderColor(new Color(210, 214, 220));
            table.addCell(th);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean zebra = false;
        for (ExportEmpleadoRow x : rows) {
            Color bg = zebra ? new Color(250, 250, 250) : Color.WHITE;
            zebra = !zebra;

            table.addCell(cell(String.valueOf(x.id()), cellFont, Element.ALIGN_RIGHT, bg));
            table.addCell(cell(n(x.numEmpleado()), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell(n(x.nombres()), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell((n(x.apellidoPaterno()) + " " + n(x.apellidoMaterno())).trim(), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell(n(x.email()), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell(n(x.departamento()), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell(n(x.puesto()), cellFont, Element.ALIGN_LEFT, bg));
            table.addCell(cell(x.fechaIngreso() != null ? x.fechaIngreso().format(fmt) : "", cellFont, Element.ALIGN_CENTER, bg));
            table.addCell(cell(Boolean.TRUE.equals(x.activo()) ? "Sí" : "No", cellFont, Element.ALIGN_CENTER, bg));
        }
        return table;
    }

    private static PdfPCell cell(String text, com.lowagie.text.Font font, int align, Color bg) {
        PdfPCell td = new PdfPCell(new Phrase(text, font));
        td.setHorizontalAlignment(align);
        td.setVerticalAlignment(Element.ALIGN_MIDDLE);
        td.setPadding(5f);
        td.setBackgroundColor(bg);
        td.setBorderColor(new Color(230, 230, 230));
        return td;
    }

    private static String n(String v) { return v == null ? "" : v; }

    // ========= Page Event para header/footer =========
    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final Options opts;
        private Image logoImg;

        HeaderFooterPageEvent(Options opts) {
            this.opts = opts;
            // Intenta cargar logo del classpath si viene opción
            if (opts.logoClasspath != null) {
                try {
                    URL url = Thread.currentThread().getContextClassLoader().getResource(opts.logoClasspath);
                    if (url != null) {
                        logoImg = Image.getInstance(url);
                        logoImg.scaleToFit(120, 40);
                    }
                } catch (Exception ignored) { }
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Rectangle page = document.getPageSize();

            // Header
            PdfPTable header = new PdfPTable(2);
            try {
                header.setWidths(new float[]{ 20f, 80f });
                header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());

                PdfPCell left = new PdfPCell();
                left.setBorder(Rectangle.NO_BORDER);
                if (logoImg != null) {
                    logoImg.setAlignment(Image.ALIGN_LEFT);
                    left.addElement(logoImg);
                }
                header.addCell(left);

                String title = opts.titulo != null ? opts.titulo : "Listado de Empleados";
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
                com.lowagie.text.Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);

                PdfPCell right = new PdfPCell();
                right.setBorder(Rectangle.NO_BORDER);
                right.setHorizontalAlignment(Element.ALIGN_RIGHT);
                right.addElement(new Phrase(title, titleFont));
                right.addElement(new Phrase((opts.empresa != null ? opts.empresa + " • " : "") + fecha, subFont));
                header.addCell(right);

                header.writeSelectedRows(0, -1, document.leftMargin(),
                        page.getTop() - 10, cb);
            } catch (Exception ignored) { }

            // Footer (número de página)
            String pageText = "Página " + writer.getPageNumber();
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase(pageText, FontFactory.getFont(FontFactory.HELVETICA, 9)),
                    (page.getLeft() + page.getRight()) / 2, page.getBottom() + 20, 0);
        }
    }
}

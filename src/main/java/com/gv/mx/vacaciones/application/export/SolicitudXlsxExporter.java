package com.gv.mx.vacaciones.application.export;

import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.OutputStream;
import java.util.List;

public class SolicitudXlsxExporter {
    public static void write(List<SolicitudVacaciones> rows, OutputStream out) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Solicitudes");
            String[] headers = {"ID","Empleado","Año","Desde","Hasta","Días","Estado","Motivo"};
            Row h = sh.createRow(0);
            for (int i=0;i<headers.length;i++) h.createCell(i).setCellValue(headers[i]);
            int r=1;
            for (var s: rows){
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getEmpleadoId());
                row.createCell(2).setCellValue(s.getAnio());
                row.createCell(3).setCellValue(s.getFechaDesde().toString());
                row.createCell(4).setCellValue(s.getFechaHasta().toString());
                row.createCell(5).setCellValue(s.getDias());
                row.createCell(6).setCellValue(s.getEstado().name());
                row.createCell(7).setCellValue(s.getMotivo()==null?"":s.getMotivo());
            }
            for (int i=0;i<headers.length;i++) sh.autoSizeColumn(i);
            wb.write(out);
        }
    }
}

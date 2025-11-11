package com.gv.mx.reportes.application.impl;

import com.gv.mx.reportes.application.JasperService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Service
public class JasperServiceImpl implements JasperService {

    @Override
    public byte[] exportPdf(String jrxmlClasspath, Map<String, Object> params) {
        try (InputStream in = new ClassPathResource(jrxmlClasspath).getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(in);

            JRDataSource ds = new JREmptyDataSource(); // sin engine.data
            Map<String,Object> prms = (params != null) ? params : Collections.emptyMap();

            JasperPrint print = JasperFillManager.fillReport(report, prms, ds);
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF Jasper: " + e.getMessage(), e);
        }
    }
}

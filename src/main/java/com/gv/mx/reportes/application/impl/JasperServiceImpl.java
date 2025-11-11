package com.gv.mx.reportes.application.impl;

import com.gv.mx.reportes.application.JasperService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JasperServiceImpl implements JasperService {

    private final ConcurrentHashMap<String, JasperReport> cache = new ConcurrentHashMap<>();

    @Override
    public byte[] renderPdfFromBeans(String jrxmlOnClasspath, Map<String, Object> params, java.util.Collection<?> beans) throws Exception {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(beans != null ? beans : java.util.List.of());
        return renderPdf(jrxmlOnClasspath, params, ds);
    }

    @Override
    public byte[] renderPdf(String jrxmlOnClasspath, Map<String, Object> params, JRDataSource dataSource) throws Exception {
        JasperReport report = cache.computeIfAbsent(jrxmlOnClasspath, this::compileUnchecked);
        JasperPrint print = JasperFillManager.fillReport(report, params != null ? params : Map.of(),
                dataSource != null ? dataSource : new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(print);
    }

    private JasperReport compileUnchecked(String jrxmlPath) {
        try (InputStream in = new ClassPathResource(jrxmlPath).getInputStream()) {
            return JasperCompileManager.compileReport(in);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo compilar JRXML: " + jrxmlPath, e);
        }
    }
}

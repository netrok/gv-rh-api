package com.gv.mx.reportes.application;

import net.sf.jasperreports.engine.JRDataSource;

import java.util.Map;

public interface JasperService {
    byte[] renderPdfFromBeans(String jrxmlOnClasspath, Map<String, Object> params, java.util.Collection<?> beans) throws Exception;
    byte[] renderPdf(String jrxmlOnClasspath, Map<String, Object> params, JRDataSource dataSource) throws Exception;
}

// src/main/java/com/gv/mx/reportes/application/JasperService.java
package com.gv.mx.reportes.application;

import java.util.Map;

public interface JasperService {
    byte[] exportPdf(String jrxmlClasspath, Map<String, Object> params);
}

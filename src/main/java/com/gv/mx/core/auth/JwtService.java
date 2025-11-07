package com.gv.mx.core.auth;

import java.util.List;

public interface JwtService {
    /** Emite un Access Token con los roles indicados. */
    String issueAccess(String subject, List<String> roles);

    /** Emite un Refresh Token para el sujeto indicado. */
    String issueRefresh(String subject);

    /** Compatibilidad: emite Access Token (equivalente a issueAccess). */
    String issue(String subject, String... roles);

    /** Extrae la lista de roles desde un JWT (roles o scope). */
    List<String> getRoles(String token);
}

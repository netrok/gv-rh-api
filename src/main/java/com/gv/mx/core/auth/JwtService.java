// src/main/java/com/gv/mx/core/auth/JwtService.java
package com.gv.mx.core.auth;

import java.util.List;

public interface JwtService {
    String issue(String username, String... roles);
    List<String> getRoles(String token);
}

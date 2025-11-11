// src/main/java/com/gv/mx/core/security/PasswordConfig.java
package com.gv.mx.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 10 es un buen balance; súbelo si quieres más costo computacional.
        return new BCryptPasswordEncoder(10);
    }
}

package com.gv.mx.core.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthDebugController {
    @GetMapping("/whoami")
    public Map<String, Object> whoami(Authentication auth) {
        return Map.of(
                "name", auth.getName(),
                "authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }
}

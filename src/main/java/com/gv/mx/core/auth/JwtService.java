package com.gv.mx.core.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface JwtService {
    String generateAccess(Authentication auth);
    String generateAccess(UserDetails user);
    String generateRefresh(UserDetails user, UUID familyId, UUID jti);
    Jwt decode(String token);
}

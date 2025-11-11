// src/main/java/com/gv/mx/core/auth/DbUserDetailsService.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.repo.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public DbUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Roles -> ROLE_*
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (u.getRoles() != null) {
            authorities.addAll(
                    u.getRoles().stream()
                            .filter(Objects::nonNull)
                            .map(r -> r.getName())
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(s -> "ROLE_" + s.toUpperCase(Locale.ROOT))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet())
            );
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Flags (por defecto todo OK; si tienes getters específicos, dímelos y los integramos)
        boolean accountNonExpired = true;
        boolean accountNonLocked = true;
        boolean credentialsNonExpired = true;
        boolean enabled = true;

        return User.withUsername(u.getUsername())
                .password(u.getPassword())              // debe estar encriptado (BCrypt)
                .authorities(authorities)
                .accountExpired(!accountNonExpired)
                .accountLocked(!accountNonLocked)
                .credentialsExpired(!credentialsNonExpired)
                .disabled(!enabled)
                .build();
    }
}

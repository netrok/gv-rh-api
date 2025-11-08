package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public DbUserDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        var authorities = u.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .disabled(!u.isEnabled())
                .build();
    }
}

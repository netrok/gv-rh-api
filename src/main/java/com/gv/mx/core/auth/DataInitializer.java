package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.Role;
import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.repo.RoleRepository;
import com.gv.mx.core.auth.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAuth(RoleRepository roles, UserRepository users, PasswordEncoder enc) {
        return args -> {
            Role admin = roles.findByName("ADMIN").orElseGet(() -> roles.save(new Role("ADMIN")));
            Role rrhh  = roles.findByName("RRHH").orElseGet(() -> roles.save(new Role("RRHH")));

            if (!users.existsByUsername("admin")) {
                UserAccount u = new UserAccount();
                u.setUsername("admin");
                u.setPassword(enc.encode("Admin123*"));
                u.getRoles().add(admin);
                u.getRoles().add(rrhh);
                users.save(u);
            }
            if (!users.existsByUsername("rrhh")) {
                UserAccount u = new UserAccount();
                u.setUsername("rrhh");
                u.setPassword(enc.encode("Rrhh123*"));
                u.getRoles().add(rrhh);
                users.save(u);
            }
        };
    }
}

package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.Role;
import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.dto.SignUpDtos.CreateUserRequest;
import com.gv.mx.core.auth.dto.SignUpDtos.UserResponse;
import com.gv.mx.core.auth.repo.RoleRepository;
import com.gv.mx.core.auth.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder enc;

    public UserAdminService(UserRepository users, RoleRepository roles, PasswordEncoder enc) {
        this.users = users;
        this.roles = roles;
        this.enc = enc;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        if (users.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username ya existe: " + req.username);
        }
        UserAccount u = new UserAccount();
        u.setUsername(req.username);
        u.setPassword(enc.encode(req.password));
        u.setEnabled(true);

        if (req.roles != null) {
            for (String r : req.roles) {
                Role role = roles.findByName(r)
                        .orElseThrow(() -> new IllegalArgumentException("Rol inexistente: " + r));
                u.getRoles().add(role);
            }
        }
        users.save(u);

        List<String> outRoles = u.getRoles().stream().map(Role::getName).sorted().toList();
        return new UserResponse(u.getId(), u.getUsername(), u.isEnabled(), outRoles);
    }
}

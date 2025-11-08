// src/main/java/com/gv/mx/core/auth/UserAdminService.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.Role;
import com.gv.mx.core.auth.domain.UserAccount;
import com.gv.mx.core.auth.dto.SignUpDtos.CreateUserRequest;
import com.gv.mx.core.auth.dto.SignUpDtos.UserResponse;
import com.gv.mx.core.auth.dto.UsersDtos.UserView;
import com.gv.mx.core.auth.dto.UsersDtos.RolesUpdateRequest;
import com.gv.mx.core.auth.repo.RoleRepository;
import com.gv.mx.core.auth.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.*; // BAD_REQUEST, CONFLICT, NOT_FOUND, UNAUTHORIZED

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

    // --------- Alta de usuario ----------
    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        if (users.existsByUsername(req.username)) {
            throw new ResponseStatusException(CONFLICT, "Username ya existe: " + req.username);
        }
        UserAccount u = new UserAccount();
        u.setUsername(req.username);
        u.setPassword(enc.encode(req.password));
        u.setEnabled(true);

        if (req.roles != null) {
            for (String r : req.roles) {
                Role role = roles.findByName(r)
                        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol inexistente: " + r));
                u.getRoles().add(role);
            }
        }
        users.save(u);

        List<String> outRoles = u.getRoles().stream().map(Role::getName).sorted().toList();
        return new UserResponse(u.getId(), u.getUsername(), u.isEnabled(), outRoles);
    }

    // --------- Listado / detalle ----------
    @Transactional
    public List<UserView> listUsers() {
        return users.findAll().stream()
                .sorted(Comparator.comparing(UserAccount::getUsername))
                .map(u -> new UserView(
                        u.getId(),
                        u.getUsername(),
                        u.isEnabled(),
                        u.getRoles().stream().map(Role::getName).sorted().toList()
                ))
                .toList();
    }

    @Transactional
    public UserView getUser(Long id) {
        UserAccount u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        return new UserView(
                u.getId(),
                u.getUsername(),
                u.isEnabled(),
                u.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    // --------- Cambios de contraseña ----------
    @Transactional
    public void changeOwnPassword(String username, String current, String next) {
        UserAccount u = users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        if (!enc.matches(current, u.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Password actual incorrecto");
        }
        u.setPassword(enc.encode(next));
        users.save(u);
    }

    @Transactional
    public void adminChangePassword(String targetUsername, String next) {
        UserAccount u = users.findByUsername(targetUsername)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        u.setPassword(enc.encode(next));
        users.save(u);
    }

    // --------- Habilitar / deshabilitar ----------
    @Transactional
    public UserView enableUser(Long id) {
        UserAccount u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        if (!u.isEnabled()) {
            u.setEnabled(true);
            users.save(u);
        }
        return new UserView(
                u.getId(), u.getUsername(), u.isEnabled(),
                u.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    @Transactional
    public UserView disableUser(Long id) {
        UserAccount u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        if (u.isEnabled()) {
            u.setEnabled(false);
            users.save(u);
        }
        return new UserView(
                u.getId(), u.getUsername(), u.isEnabled(),
                u.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    // --------- Roles (agregar / quitar) ----------
    @Transactional
    public UserView addRoles(Long id, RolesUpdateRequest req) {
        if (req == null || req.roles == null || req.roles.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "roles no puede estar vacío");
        }
        UserAccount u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));

        for (String r : req.roles) {
            Role role = roles.findByName(r)
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol inexistente: " + r));
            u.getRoles().add(role);
        }
        users.save(u);

        return new UserView(
                u.getId(), u.getUsername(), u.isEnabled(),
                u.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    @Transactional
    public UserView removeRole(Long id, String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "role es requerido");
        }
        UserAccount u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));

        Role role = roles.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol inexistente: " + roleName));

        boolean removed = u.getRoles().remove(role);
        if (removed) users.save(u);

        return new UserView(
                u.getId(), u.getUsername(), u.isEnabled(),
                u.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }
}

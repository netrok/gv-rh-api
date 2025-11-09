package com.gv.mx.catalogos.service.impl;

import com.gv.mx.catalogos.domain.Banco;
import com.gv.mx.catalogos.repo.BancoRepository;
import com.gv.mx.catalogos.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BancoServiceImpl implements BancoService {

    private final BancoRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Page<Banco> listar(String q, Pageable pageable) {
        String query = (q == null) ? null : q.trim();
        return (query == null || query.isEmpty())
                ? repo.findAll(pageable)
                : repo.findByNombreContainingIgnoreCase(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Banco obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banco no encontrado: " + id));
    }

    @Override
    @Transactional
    public Banco guardar(Banco data) {
        String nombre = safeNombre(data.getNombre());
        if (repo.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe banco con nombre: " + nombre);
        }
        data.setId(null);
        data.setNombre(nombre);
        return repo.save(data);
    }

    @Override
    @Transactional
    public Banco actualizar(Long id, Banco data) {
        var entity = obtener(id);
        String nuevoNombre = safeNombre(data.getNombre());

        // Solo validar duplicado si realmente cambia (comparando trim + ignoreCase)
        if (!entity.getNombre().trim().equalsIgnoreCase(nuevoNombre)
                && repo.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new IllegalArgumentException("Ya existe banco con nombre: " + nuevoNombre);
        }

        entity.setNombre(nuevoNombre);
        entity.setActivo(Boolean.TRUE.equals(data.getActivo()));
        return repo.save(entity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        try {
            if (repo.existsById(id)) {
                repo.deleteById(id);
            }
        } catch (DataIntegrityViolationException ex) {
            // Clave foránea en uso (por ejemplo, empleados.banco_id)
            throw new IllegalStateException("No se puede eliminar: el banco está referenciado por otros registros.");
        }
    }

    private String safeNombre(String raw) {
        if (raw == null) throw new IllegalArgumentException("El nombre es obligatorio.");
        String n = raw.trim();
        if (n.isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        return n;
    }
}
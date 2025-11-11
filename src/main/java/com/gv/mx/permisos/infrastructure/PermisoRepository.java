package com.gv.mx.permisos.infrastructure;

import com.gv.mx.permisos.domain.Permiso;
import com.gv.mx.permisos.domain.PermisoEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    Page<Permiso> findByEmpleadoId(Long empleadoId, Pageable pageable);
    long countByEstado(PermisoEstado estado);
}

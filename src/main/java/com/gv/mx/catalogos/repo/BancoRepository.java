package com.gv.mx.catalogos.repo;

import com.gv.mx.catalogos.domain.Banco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
    Page<Banco> findByNombreContainingIgnoreCase(String q, Pageable pageable);
    boolean existsByNombreIgnoreCase(String nombre);
}
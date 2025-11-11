package com.gv.mx.workflow.infrastructure;

import com.gv.mx.workflow.domain.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalFlowRepo extends JpaRepository<ApprovalFlow, Long> {
    Optional<ApprovalFlow> findByModuloAndEntidadId(String modulo, String entidadId);
}

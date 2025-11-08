package com.gv.mx.core.auth.repo;

import com.gv.mx.core.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(UUID jti);
    List<RefreshToken> findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(Long userId, Instant now);
    List<RefreshToken> findByUserId(Long userId);
    List<RefreshToken> findByFamilyId(UUID familyId);
}

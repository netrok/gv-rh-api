// src/main/java/com/gv/mx/core/auth/RefreshTokenService.java
package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.RefreshToken;
import com.gv.mx.core.auth.repo.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshMinutes;
    private final int maxFamiliesPerUser;

    public RefreshTokenService(RefreshTokenRepository repo, JwtProperties props) {
        this.repo = repo;
        this.refreshMinutes = props.getRefreshMinutes();
        // si no viene o es <= 0, usamos 5 por defecto
        int configured = props.getMaxFamiliesPerUser();
        this.maxFamiliesPerUser = (configured > 0) ? configured : 5;
    }

    public UUID newFamilyId() { return UUID.randomUUID(); }
    public UUID newJti() { return UUID.randomUUID(); }

    @Transactional
    public RefreshToken issue(Long userId, UUID familyId, UUID jti, String ip, String ua) {
        var now = Instant.now();

        var t = new RefreshToken();
        t.setUserId(userId);
        t.setFamilyId(familyId);
        t.setJti(jti);
        t.setIssuedAt(now);
        t.setExpiresAt(now.plus(refreshMinutes, ChronoUnit.MINUTES));
        t.setIp(ip);
        t.setUserAgent(ua);
        repo.save(t);

        // Limpiar exceso de familias activas por usuario
        var actives = repo.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, now);

        // agrupamos por familyId, conservando el más reciente (issuedAt desc)
        var byFamily = new LinkedHashMap<UUID, RefreshToken>();
        actives.stream()
                .sorted(Comparator.comparing(RefreshToken::getIssuedAt).reversed())
                .forEach(rt -> byFamily.putIfAbsent(rt.getFamilyId(), rt));

        if (byFamily.size() > maxFamiliesPerUser) {
            int toRevoke = byFamily.size() - maxFamiliesPerUser;
            var families = new ArrayList<>(byFamily.keySet());
            Collections.reverse(families); // más antiguas primero
            for (int i = 0; i < toRevoke; i++) {
                revokeFamily(families.get(i), "family_limit");
            }
        }
        return t;
    }

    @Transactional
    public void revoke(UUID jti, String reason) {
        repo.findByJti(jti).ifPresent(rt -> {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(Instant.now());
                rt.setRevokedReason(reason);
                repo.save(rt);
            }
        });
    }

    @Transactional
    public void revokeFamily(UUID familyId, String reason) {
        var list = repo.findByFamilyId(familyId);
        var now = Instant.now();
        for (var rt : list) {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(now);
                rt.setRevokedReason(reason);
            }
        }
        repo.saveAll(list);
    }

    @Transactional
    public void revokeAllByUser(Long userId, String reason) {
        var list = repo.findByUserId(userId);
        var now = Instant.now();
        for (var rt : list) {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(now);
                rt.setRevokedReason(reason);
            }
        }
        repo.saveAll(list);
    }

    @Transactional
    public RefreshToken rotateOrDetectReuse(UUID jti) {
        var opt = repo.findByJti(jti);
        if (opt.isEmpty()) throw new IllegalStateException("Refresh desconocido");

        var current = opt.get();
        var now = Instant.now();

        // expirado o revocado → cerrar familia
        if (current.getExpiresAt().isBefore(now)) {
            revokeFamily(current.getFamilyId(), "expired");
            throw new IllegalStateException("Refresh expirado");
        }
        if (current.getRevokedAt() != null) {
            // RE-USE detectado
            revokeFamily(current.getFamilyId(), "reuse_detected");
            throw new IllegalStateException("Refresh reusado (familia revocada)");
        }

        // Rotación: revocar actual, emitir nuevo en la misma familia
        current.setRevokedAt(now);
        current.setRevokedReason("rotated");
        current.setLastUsedAt(now);
        repo.save(current);

        var newJti = newJti();
        return issue(current.getUserId(), current.getFamilyId(), newJti, current.getIp(), current.getUserAgent());
    }
}

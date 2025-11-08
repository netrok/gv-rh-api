package com.gv.mx.core.auth;

import com.gv.mx.core.auth.domain.RefreshToken;
import com.gv.mx.core.auth.repo.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshMinutes;
    private final int maxFamiliesPerUser;

    public RefreshTokenService(RefreshTokenRepository repo,
                               @Value("${app.jwt.refresh-minutes}") long refreshMinutes,
                               @Value("${app.jwt.max-families-per-user:5}") int maxFamiliesPerUser) {
        this.repo = repo;
        this.refreshMinutes = refreshMinutes;
        this.maxFamiliesPerUser = maxFamiliesPerUser;
    }

    public UUID newFamilyId() { return UUID.randomUUID(); }
    public UUID newJti() { return UUID.randomUUID(); }

    @Transactional
    public RefreshToken issue(Long userId, UUID familyId, UUID jti, String ip, String ua) {
        var t = new RefreshToken();
        t.setUserId(userId);
        t.setFamilyId(familyId);
        t.setJti(jti);
        t.setIssuedAt(Instant.now());
        t.setExpiresAt(Instant.now().plus(refreshMinutes, ChronoUnit.MINUTES));
        t.setIp(ip);
        t.setUserAgent(ua);
        repo.save(t);

        // Enforce límite de familias activas por usuario (limpieza simple)
        var actives = repo.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, Instant.now());
        // Agrupar por familyId y dejar solo las más recientes (por issuedAt)
        var byFamily = new LinkedHashMap<UUID, RefreshToken>();
        actives.stream()
                .sorted(Comparator.comparing(RefreshToken::getIssuedAt).reversed())
                .forEach(rt -> byFamily.putIfAbsent(rt.getFamilyId(), rt));

        if (byFamily.size() > maxFamiliesPerUser) {
            int toRevoke = byFamily.size() - maxFamiliesPerUser;
            var families = new ArrayList<>(byFamily.keySet());
            Collections.reverse(families); // más antiguas al inicio
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

        // expirado o revocado → cerrar familia
        if (current.getExpiresAt().isBefore(Instant.now())) {
            revokeFamily(current.getFamilyId(), "expired");
            throw new IllegalStateException("Refresh expirado");
        }
        if (current.getRevokedAt() != null) {
            // RE-USE: alguien intentó usar un token ya rotado/revocado
            revokeFamily(current.getFamilyId(), "reuse_detected");
            throw new IllegalStateException("Refresh reusado (familia revocada)");
        }

        // Rotación: revocar actual y emitir nuevo en misma familia
        current.setRevokedAt(Instant.now());
        current.setRevokedReason("rotated");
        current.setLastUsedAt(Instant.now());
        repo.save(current);

        var newJti = newJti();
        return issue(current.getUserId(), current.getFamilyId(), newJti, current.getIp(), current.getUserAgent());
    }
}

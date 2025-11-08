CREATE TABLE IF NOT EXISTS auth_refresh_tokens (
                                                   id             BIGSERIAL PRIMARY KEY,
                                                   jti            UUID NOT NULL UNIQUE,
                                                   family_id      UUID NOT NULL,
                                                   user_id        BIGINT NOT NULL,
                                                   issued_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_used_at   TIMESTAMP,
    expires_at     TIMESTAMP NOT NULL,
    revoked_at     TIMESTAMP,
    revoked_reason VARCHAR(200),
    ip             VARCHAR(100),
    user_agent     VARCHAR(300)
    );

CREATE INDEX IF NOT EXISTS ix_refresh_user    ON auth_refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS ix_refresh_family  ON auth_refresh_tokens(family_id);
CREATE INDEX IF NOT EXISTS ix_refresh_expires ON auth_refresh_tokens(expires_at);

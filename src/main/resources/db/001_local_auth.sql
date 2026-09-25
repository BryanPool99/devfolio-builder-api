-- 001_local_auth.sql
-- Migracion de Neon Auth a autenticacion propia (Spring Security + JWT RS256).
-- Ejecutar manualmente en la base de datos de Neon.
-- Es idempotente: se puede volver a ejecutar sin efectos.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password_hash  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS role           VARCHAR(20) NOT NULL DEFAULT 'USER',
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- auth_id pasa a ser el identificador propio que el backend emite como claim 'sub'.
-- Los usuarios creados con Neon Auth quedan con el sub de Neon hasta que se purguen.
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_auth_id ON users (auth_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_username ON users (lower(username));

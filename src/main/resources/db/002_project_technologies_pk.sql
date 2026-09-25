-- 002_project_technologies_pk.sql
-- project_technologies se creo sin clave primaria y Spring Data R2DBC la exige
-- para mapear la entidad.
--
-- Es idempotente: se puede ejecutar mas de una vez sin fallar.
-- Si hay pares (project_id, technology_id) duplicados se detiene con un mensaje
-- explicito en vez de fallar con una violacion de indice unico.

-- 1. Columna id. BIGSERIAL crea la secuencia y el DEFAULT, por lo que las filas
--    existentes quedan rellenadas automaticamente.
ALTER TABLE project_technologies
    ADD COLUMN IF NOT EXISTS id BIGSERIAL;

-- 2. Red de seguridad: solo actua si quedo alguna fila sin id (por ejemplo si la
--    columna existia previa y sin DEFAULT). Usa la secuencia de la propia tabla.
DO $$
DECLARE
    seq text;
BEGIN
    IF EXISTS (SELECT 1 FROM project_technologies WHERE id IS NULL) THEN
        seq := pg_get_serial_sequence('project_technologies', 'id');
        IF seq IS NULL THEN
            RAISE EXCEPTION
                'project_technologies tiene filas sin id y no hay secuencia para rellenarlas. Revisa la tabla antes de continuar.';
        END IF;
        EXECUTE format('UPDATE project_technologies SET id = nextval(%L) WHERE id IS NULL', seq);
    END IF;
END $$;

-- 3. La columna pasa a ser obligatoria.
ALTER TABLE project_technologies
    ALTER COLUMN id SET NOT NULL;

-- 4. Clave primaria, solo si aun no existe.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'project_technologies_pkey'
          AND conrelid = 'project_technologies'::regclass
    ) THEN
        ALTER TABLE project_technologies
            ADD CONSTRAINT project_technologies_pkey PRIMARY KEY (id);
    END IF;
END $$;

-- 5. Comprobacion previa al indice unico, para fallar con un mensaje util.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM project_technologies
        GROUP BY project_id, technology_id
        HAVING count(*) > 1
    ) THEN
        RAISE EXCEPTION
            'project_technologies tiene pares (project_id, technology_id) duplicados. Eliminalos antes de crear ux_project_technologies_pair.';
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_project_technologies_pair
    ON project_technologies (project_id, technology_id);

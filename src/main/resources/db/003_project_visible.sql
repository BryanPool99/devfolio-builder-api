-- 003_project_visible.sql
-- Visibilidad por proyecto: permite ocultar proyectos del portafolio publico
-- sin eliminarlos y sin tocar el limite numerico del bloque PROJECTS.
-- El CRUD autenticado (GET /portfolio/projects) sigue listando todos;
-- el endpoint publico solo devuelve los proyectos con visible = TRUE.
-- Ejecutar manualmente en la base de datos de Neon.
-- Es idempotente: se puede volver a ejecutar sin efectos.

-- ADD COLUMN con DEFAULT rellena las filas existentes con TRUE, por lo que
-- ningun proyecto publicado deja de verse tras aplicar la migracion.
ALTER TABLE projects
    ADD COLUMN IF NOT EXISTS visible BOOLEAN NOT NULL DEFAULT TRUE;

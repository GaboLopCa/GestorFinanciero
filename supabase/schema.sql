-- =============================================================
-- GestorFinanciero: Hardening de seguridad para Supabase
-- -------------------------------------------------------------
-- EJECUTAR EN EL SQL EDITOR DE SUPABASE ÚNICAMENTE DESPUÉS DEL
-- PRIMER DEPLOY cuando Hibernate haya creado las 7 tablas.
-- Es idempotente: se puede ejecutar las veces que se necesite.
--
-- Propósito:
--   1) Bloquear las roles públicas de PostgREST (anon / authenticated).
--   2) Activar ROW LEVEL SECURITY en todas las tablas de la app.
-- La app Spring Boot conecta como postgres (superusuario) y NO se
-- ve afectada por RLS.
-- =============================================================

-- 1) Revocar privilegios existentes de las roles públicas
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM anon, authenticated;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM anon, authenticated;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM anon, authenticated;

-- 2) Evitar que tablas futuras otorguen acceso a anon/authenticated
ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON TABLES FROM anon, authenticated;
ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON SEQUENCES FROM anon, authenticated;
ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON FUNCTIONS FROM anon, authenticated;

-- 3) Activar Row Level Security en las 7 tablas de la aplicación
ALTER TABLE public.USUARIOS ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.GASTO ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.INGRESOS ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.GASTOS_FIJOS ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.TRANSACCIONES ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.INVERSIONES ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.FONDO_MUTUO_DETALLES ENABLE ROW LEVEL SECURITY;

-- Verificación sugerida:
-- SELECT schemaname, tablename, rowsecurity FROM pg_tables
--   WHERE schemaname = 'public' AND rowsecurity = false;
-- No debería devolver ninguna fila de las tablas de la app.
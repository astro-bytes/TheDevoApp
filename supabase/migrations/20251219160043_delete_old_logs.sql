-- ============================================================
-- PG_CRON EXTENSION
-- ============================================================
-- Enables scheduling of periodic jobs directly in the database.
-- Used here to schedule log pruning based on retention policy.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pg_cron;

-- ============================================================
-- LOG RETENTION CONFIGURATION
-- ============================================================
-- This configuration controls how long log data is retained
-- in the database. Changing this value does NOT require a
-- migration or redeploy — updates take effect immediately.
--
-- value: Number of days to keep log_events records
-- example: '7' = keep logs for the last 7 days
-- ============================================================

INSERT INTO configurations (
    platform,
    namespace,
    property_name,
    value,
    description
) VALUES (
    'server',
    'logging',
    'retention_days',
    '7',
    'Number of days to retain log_events'
);

-- ============================================================
-- LOG PRUNING FUNCTION
-- ============================================================
-- Deletes log_events older than the configured retention period.
--
-- Behavior:
-- 1. Reads the retention period (in days) from the configurations table
-- 2. If no configuration is found, the function exits safely
-- 3. Deletes log records older than (now - retention_days)
--
-- This function is intended to be run on a schedule (cron),
-- not on every insert, to avoid performance impact.
-- ============================================================

CREATE OR REPLACE FUNCTION prune_log_events()
RETURNS void
LANGUAGE plpgsql
SET search_path = public
AS $$
DECLARE
    -- Number of days logs should be retained
    retention_days INTEGER;
BEGIN
    -- Fetch retention period from configuration
    SELECT (value::text)::INTEGER
    INTO retention_days
    FROM configurations
    WHERE platform = 'server'
      AND namespace = 'logging'
      AND property_name = 'retention_days';

    -- If no retention configuration exists, do nothing
    IF retention_days IS NULL THEN
        RAISE NOTICE 'No retention policy configured; skipping prune';
        RETURN;
    END IF;

    -- Delete logs older than the configured retention window
    DELETE FROM log_events
    WHERE occurred_at < now() - (retention_days || ' days')::INTERVAL;
END;
$$;

-- ============================================================
-- SCHEDULED CRON JOB
-- ============================================================
-- Runs the log pruning function once per day at midnight.
--
-- Notes:
-- - Uses pg_cron (Supabase supported)
-- - Runs in the database time zone (typically UTC)
-- - Automatically enforces retention without app involvement
-- ============================================================

SELECT
  cron.schedule(
    'prune-log-events',
    '0 0 * * *',  -- Daily at midnight
    $$SELECT prune_log_events();$$
  );

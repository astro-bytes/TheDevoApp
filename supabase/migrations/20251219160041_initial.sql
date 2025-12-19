-- =============================================================
-- SPEAKERS
-- =============================================================
CREATE TABLE speakers (
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    middle_initial TEXT,
    last_name TEXT NOT NULL,
    profession TEXT
);

-- =============================================================
-- SCRIPTURES
-- =============================================================
CREATE TABLE scriptures (
    id SERIAL PRIMARY KEY,
    book TEXT NOT NULL,
    chapter TEXT NOT NULL,
    verses JSONB NOT NULL,
    url TEXT,

    CONSTRAINT verses_is_int_array CHECK (
        jsonb_typeof(verses) = 'array'
        AND jsonb_path_match(
            verses,
            '$[*] ? (@.type() == "number" && @ like_regex "^[1-9][0-9]*$")'
        )
    )
);

-- =============================================================
-- DEVOTIONALS
-- =============================================================
CREATE TABLE devotionals (
    id SERIAL PRIMARY KEY,
    date_started TIMESTAMPTZ NOT NULL,
    prelude_music TEXT,
    invocation TEXT,
    opening_music TEXT,
    scripture_id INTEGER,
    speaker_id INTEGER NOT NULL,
    closing_music TEXT,
    benediction TEXT,
    postlude_music TEXT,
    date_ended TIMESTAMPTZ,
    summary TEXT,
    transcript TEXT,

    CONSTRAINT fk_devotional_scripture
        FOREIGN KEY (scripture_id)
        REFERENCES scriptures (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_devotional_speaker
        FOREIGN KEY (speaker_id)
        REFERENCES speakers (id)
        ON DELETE RESTRICT
);

-- =============================================================
-- TOPICS
-- =============================================================
CREATE TABLE topics (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE UNIQUE INDEX topics_name_lower_unique
ON topics (LOWER(name));

-- =============================================================
-- DEVOTIONAL_TOPICS
-- =============================================================
CREATE TABLE devotional_topics (
    devotional_id INTEGER NOT NULL,
    topic_id INTEGER NOT NULL,

    PRIMARY KEY (devotional_id, topic_id),

    FOREIGN KEY (devotional_id)
        REFERENCES devotionals (id)
        ON DELETE CASCADE,

    FOREIGN KEY (topic_id)
        REFERENCES topics (id)
        ON DELETE RESTRICT
);

-- =============================================================
-- TAPS
-- =============================================================
CREATE TABLE taps (
    id SERIAL PRIMARY KEY,
    anon_user_id UUID NOT NULL,
    devotional_id INTEGER NOT NULL,
    stamp TIMESTAMPTZ NOT NULL,

    FOREIGN KEY (anon_user_id)
        REFERENCES auth.users (id)
        ON DELETE CASCADE,

    FOREIGN KEY (devotional_id)
        REFERENCES devotionals (id)
        ON DELETE CASCADE
);

-- =============================================================
-- QUOTES
-- =============================================================
CREATE TABLE quotes (
    id SERIAL PRIMARY KEY,
    devotional_id INTEGER NOT NULL,
    like_count INTEGER NOT NULL DEFAULT 0,
    tap_count INTEGER NOT NULL DEFAULT 0,
    blob TEXT NOT NULL,

    FOREIGN KEY (devotional_id)
        REFERENCES devotionals (id)
        ON DELETE CASCADE
);

-- =============================================================
-- CONFIGURATIONS
-- =============================================================
CREATE TABLE configurations (
    id BIGSERIAL PRIMARY KEY,
    platform TEXT NOT NULL,
    namespace TEXT NOT NULL,
    property_name TEXT NOT NULL,
    os_min TEXT,
    os_max TEXT,
    app_version_min TEXT,
    app_version_max TEXT,
    value JSONB NOT NULL,
    description TEXT
);

CREATE UNIQUE INDEX idx_configurations_lookup
ON configurations (platform, namespace, property_name);

-- =============================================================
-- LOGGING
-- =============================================================
CREATE TYPE log_level AS ENUM ('debug','info','warn','error','fatal');

CREATE TABLE log_events (
    id BIGSERIAL PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL,
    level log_level NOT NULL,
    source TEXT NOT NULL,
    message TEXT NOT NULL,
    context JSONB
);

CREATE TABLE log_errors (
    id BIGSERIAL PRIMARY KEY,
    log_event_id BIGINT NOT NULL,
    error_type TEXT NOT NULL,
    stack_trace TEXT,

    FOREIGN KEY (log_event_id)
        REFERENCES log_events (id)
        ON DELETE CASCADE
);

-- =============================================================
-- INDEXES
-- =============================================================
CREATE INDEX idx_devotionals_date_started ON devotionals (date_started);
CREATE INDEX idx_devotionals_speaker_id ON devotionals (speaker_id);
CREATE INDEX idx_devotionals_scripture_id ON devotionals (scripture_id);
CREATE INDEX idx_taps_user_id ON taps (anon_user_id);
CREATE INDEX idx_taps_devotional_id ON taps (devotional_id);
CREATE INDEX idx_quotes_devotional_id ON quotes (devotional_id);
CREATE INDEX idx_log_events_occurred_at ON log_events (occurred_at);
CREATE INDEX idx_log_errors_log_event_id ON log_errors (log_event_id);

-- =============================================================
-- ENABLE RLS
-- =============================================================
ALTER TABLE speakers ENABLE ROW LEVEL SECURITY;
ALTER TABLE scriptures ENABLE ROW LEVEL SECURITY;
ALTER TABLE devotionals ENABLE ROW LEVEL SECURITY;
ALTER TABLE topics ENABLE ROW LEVEL SECURITY;
ALTER TABLE devotional_topics ENABLE ROW LEVEL SECURITY;
ALTER TABLE taps ENABLE ROW LEVEL SECURITY;
ALTER TABLE quotes ENABLE ROW LEVEL SECURITY;
ALTER TABLE configurations ENABLE ROW LEVEL SECURITY;
ALTER TABLE log_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE log_errors ENABLE ROW LEVEL SECURITY;

-- =============================================================
-- RLS POLICIES
-- =============================================================

-- =========================
-- DEVOTIONALS
-- =========================

CREATE POLICY read_authenticated
ON devotionals FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY devotionals_insert
ON devotionals FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY devotionals_update
ON devotionals FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY devotionals_delete
ON devotionals FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- SPEAKERS
-- =========================

CREATE POLICY speakers_read
ON speakers FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY speakers_insert
ON speakers FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY speakers_update
ON speakers FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY speakers_delete
ON speakers FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- SCRIPTURES
-- =========================

CREATE POLICY scriptures_read
ON scriptures FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY scriptures_insert
ON scriptures FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY scriptures_update
ON scriptures FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY scriptures_delete
ON scriptures FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- TOPICS
-- =========================

CREATE POLICY topics_read
ON topics FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY topics_insert
ON topics FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY topics_update
ON topics FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY topics_delete
ON topics FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- DEVOTIONAL_TOPICS
-- =========================

CREATE POLICY devotional_topics_read
ON devotional_topics FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY devotional_topics_insert
ON devotional_topics FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY devotional_topics_update
ON devotional_topics FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY devotional_topics_delete
ON devotional_topics FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- QUOTES
-- =========================

CREATE POLICY quotes_read
ON quotes FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY quotes_insert
ON quotes FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY quotes_update
ON quotes FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY quotes_delete
ON quotes FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- TAPS
-- =========================

CREATE POLICY taps_read
ON taps FOR SELECT
USING ((SELECT auth.role()) = 'service_role');

CREATE POLICY taps_insert
ON taps FOR INSERT
WITH CHECK (anon_user_id = (SELECT auth.uid()));

CREATE POLICY taps_update
ON taps FOR UPDATE
USING (false)
WITH CHECK (false);

CREATE POLICY taps_delete
ON taps FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- CONFIGURATIONS (FIXED)
-- =========================

-- Single SELECT policy
CREATE POLICY configs_read
ON configurations FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY configs_insert
ON configurations FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY configs_update
ON configurations FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY configs_delete
ON configurations FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- LOG EVENTS
-- =========================

CREATE POLICY log_events_read
ON log_events FOR SELECT
USING ((SELECT auth.role()) = 'service_role');

CREATE POLICY log_events_insert
ON log_events FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'authenticated');

CREATE POLICY log_events_update
ON log_events FOR UPDATE
USING (false)
WITH CHECK (false);

CREATE POLICY log_events_delete
ON log_events FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- LOG ERRORS
-- =========================

CREATE POLICY log_errors_read
ON log_errors FOR SELECT
USING ((SELECT auth.role()) = 'service_role');

CREATE POLICY log_errors_insert
ON log_errors FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'authenticated');

CREATE POLICY log_errors_update
ON log_errors FOR UPDATE
USING (false)
WITH CHECK (false);

CREATE POLICY log_errors_delete
ON log_errors FOR DELETE
USING ((SELECT auth.role()) = 'service_role');
-- =============================================================
-- SPEAKERS
-- =============================================================
CREATE TABLE people (
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    middle_initial TEXT,
    last_name TEXT NOT NULL
);

-- =============================================================
-- SCRIPTURES
-- =============================================================
CREATE TABLE scriptures (
    id SERIAL PRIMARY KEY,
    reader_id INTEGER NOT NULL,
    book TEXT NOT NULL,
    chapter TEXT NOT NULL,
    verses JSONB NOT NULL, -- JSONB array of integers
    url TEXT,

    FOREIGN KEY (reader_id)
        REFERENCES people (id)
        ON DELETE RESTRICT
);

-- =============================================================
-- MUSIC
-- =============================================================
CREATE TABLE music (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    composer TEXT,
    arranger TEXT,
    url TEXT
);

-- =============================================================
-- PERFORMANCES
-- =============================================================
CREATE TABLE performances (
    id SERIAL PRIMARY KEY,
    music_id INTEGER NOT NULL,
    performer_id INTEGER NOT NULL,

    FOREIGN KEY (music_id)
        REFERENCES music (id)
        ON DELETE CASCADE,

    FOREIGN KEY (performer_id)
        REFERENCES people (id)
        ON DELETE RESTRICT
);
-- =============================================================
-- DEVOTIONALS
-- =============================================================
CREATE TABLE devotionals (
    id SERIAL PRIMARY KEY,
    date_started TIMESTAMPTZ NOT NULL,
    prelude_id INTEGER,
    invocation_id INTEGER,
    introit_id INTEGER,
    scripture_id INTEGER,
    speaker_id INTEGER NOT NULL,
    postlude_id INTEGER,
    benediction_id INTEGER,
    recessional_id INTEGER,
    date_ended TIMESTAMPTZ NOT NULL,
    title TEXT NOT NULL,
    summary TEXT,
    transcript TEXT,
    url TEXT,

    CONSTRAINT fk_devotional_prelude_performance
        FOREIGN KEY (prelude_id)
        REFERENCES performances (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_invocation
        FOREIGN KEY (invocation_id)
        REFERENCES people (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_introit
        FOREIGN KEY (introit_id)
        REFERENCES performances (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_scripture
        FOREIGN KEY (scripture_id)
        REFERENCES scriptures (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_speaker
        FOREIGN KEY (speaker_id)
        REFERENCES people (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_devotional_postlude_performance
        FOREIGN KEY (postlude_id)
        REFERENCES performances (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_benediction
        FOREIGN KEY (benediction_id)
        REFERENCES people (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_devotional_recessional_performance
        FOREIGN KEY (recessional_id)
        REFERENCES performances (id)
        ON DELETE SET NULL
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
ALTER TABLE people ENABLE ROW LEVEL SECURITY;
ALTER TABLE scriptures ENABLE ROW LEVEL SECURITY;
ALTER TABLE music ENABLE ROW LEVEL SECURITY;
ALTER TABLE performances ENABLE ROW LEVEL SECURITY;
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

CREATE POLICY devotionals_read
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
-- PEOPLE
-- =========================

CREATE POLICY people_read
ON people FOR SELECT
USING (
    (SELECT auth.role()) IN ('authenticated', 'service_role')
);

CREATE POLICY people_insert
ON people FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY people_update
ON people FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY people_delete
ON people FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- MUSIC
-- =========================

ALTER TABLE music ENABLE ROW LEVEL SECURITY;

CREATE POLICY music_read
ON music FOR SELECT
USING ((SELECT auth.role()) IN ('authenticated', 'service_role'));

CREATE POLICY music_insert
ON music FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY music_update
ON music FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY music_delete
ON music FOR DELETE
USING ((SELECT auth.role()) = 'service_role');

-- =========================
-- PERFORMANCES
-- =========================

ALTER TABLE performances ENABLE ROW LEVEL SECURITY;

CREATE POLICY performances_read
ON performances FOR SELECT
USING ((SELECT auth.role()) IN ('authenticated', 'service_role'));

CREATE POLICY performances_insert
ON performances FOR INSERT
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY performances_update
ON performances FOR UPDATE
USING ((SELECT auth.role()) = 'service_role')
WITH CHECK ((SELECT auth.role()) = 'service_role');

CREATE POLICY performances_delete
ON performances FOR DELETE
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
-- CONFIGURATIONS
-- =========================

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
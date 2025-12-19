-- =====================================================
-- GENERATE SPEAKERS
-- =====================================================

INSERT INTO speakers (first_name, middle_initial, last_name, profession)
SELECT
    -- First names (cycled)
    (ARRAY[
        'John','Sarah','Michael','Emily','David','Rachel','Daniel','Anna',
        'James','Laura','Robert','Hannah','Joseph','Emma','Matthew','Olivia',
        'Andrew','Sophia','Benjamin','Grace'
    ])[((g - 1) % 20) + 1] AS first_name,

    -- Middle initial (NULL every 5th row)
    CASE
        WHEN g % 5 = 0 THEN NULL
        ELSE chr(65 + (g % 26))
    END AS middle_initial,

    -- Last names (cycled)
    (ARRAY[
        'Smith','Johnson','Brown','Taylor','Anderson','Thomas','Jackson','White',
        'Harris','Martin','Thompson','Garcia','Martinez','Robinson','Clark',
        'Rodriguez','Lewis','Lee','Walker','Hall'
    ])[((g - 1) % 20) + 1] AS last_name,

    -- Professions (cycled)
    (ARRAY[
        'Professor','Engineer','Teacher','Researcher','Minister','Author',
        'Scientist','Developer','Designer','Analyst'
    ])[((g - 1) % 10) + 1] AS profession

FROM generate_series(1, 500) AS g;

-- =====================================================
-- GENERATE SCRIPTURES
-- =====================================================

INSERT INTO scriptures (book, chapter, verses, url)
SELECT
    book,
    chapter,
    verses,
    url
FROM (
    SELECT
        g,

        -- Rotate through standard works
        CASE (g % 4)
            WHEN 0 THEN 'Genesis'
            WHEN 1 THEN '1 Nephi'
            WHEN 2 THEN 'Doctrine and Covenants'
            ELSE 'Moses'
        END AS book,

        -- Chapter / section
        CASE (g % 4)
            WHEN 0 THEN ((g % 50) + 1)::TEXT                     -- Genesis 1–50
            WHEN 1 THEN ((g % 22) + 1)::TEXT                     -- 1 Nephi 1–22
            WHEN 2 THEN ((g % 138) + 1)::TEXT                    -- D&C 1–138
            ELSE ((g % 8) + 1)::TEXT                             -- Moses 1–8
        END AS chapter,

        -- Verses: always a JSONB integer array
        CASE
            WHEN g % 3 = 0 THEN jsonb_build_array(1)
            WHEN g % 3 = 1 THEN jsonb_build_array(1, 2)
            ELSE jsonb_build_array(1, 2, 3)
        END AS verses,

        -- LDS scripture URLs
        CASE (g % 4)
            WHEN 0 THEN
                'https://www.churchofjesuschrist.org/study/scriptures/ot/gen/'
                || ((g % 50) + 1)
            WHEN 1 THEN
                'https://www.churchofjesuschrist.org/study/scriptures/bofm/1-ne/'
                || ((g % 22) + 1)
            WHEN 2 THEN
                'https://www.churchofjesuschrist.org/study/scriptures/dc-testament/dc/'
                || ((g % 138) + 1)
            ELSE
                'https://www.churchofjesuschrist.org/study/scriptures/pgp/moses/'
                || ((g % 8) + 1)
        END AS url

    FROM generate_series(1, 500) AS g
) s;

-- =====================================================
-- GENERATE TOPICS
-- =====================================================

INSERT INTO topics (name) VALUES
('Faith'),
('Repentance'),
('Atonement of Jesus Christ'),
('Prayer'),
('Personal Revelation'),
('Scripture Study'),
('Covenants'),
('Obedience'),
('Grace'),
('Charity'),
('Service'),
('Hope'),
('Trust in the Lord'),
('Enduring to the End'),
('Agency'),
('Forgiveness'),
('Humility'),
('Discipleship'),
('Conversion'),
('Testimony'),
('Eternal Life'),
('Joy'),
('Peace'),
('Love'),
('Faith in Trials'),
('Spiritual Growth'),
('Revelation through the Holy Ghost'),
('Keeping Commandments'),
('Following Jesus Christ'),
('Building Zion');

-- =====================================================
-- GENERATE DEVOTIONALS
-- =====================================================

WITH base_tuesday AS (
    -- Most recent Tuesday at 11:30 AM
    SELECT
        date_trunc('week', now()) + interval '1 day 11 hours 30 minutes' AS tuesday_1130
),
series AS (
    SELECT
        gs.n,
        CASE
            WHEN gs.n < 250 THEN 'past'
            ELSE 'future'
        END AS timing,
        CASE
            WHEN gs.n < 250
                THEN b.tuesday_1130 - (gs.n * interval '7 days')
            ELSE
                b.tuesday_1130 + ((gs.n - 249) * interval '7 days')
        END AS start_time
    FROM generate_series(0, 499) AS gs(n)
    CROSS JOIN base_tuesday b
)
INSERT INTO devotionals (
    date_started,
    prelude_music,
    invocation,
    opening_music,
    scripture_id,
    speaker_id,
    closing_music,
    benediction,
    postlude_music,
    date_ended,
    summary,
    transcript
)
SELECT
    s.start_time AS date_started,

    -- Past devotionals only
    CASE WHEN s.timing = 'past' THEN 'Prelude Hymn ' || (random() * 300)::int END,
    CASE WHEN s.timing = 'past' THEN 'Opening prayer offered with reverence.' END,
    CASE WHEN s.timing = 'past' THEN 'Opening Hymn ' || (random() * 300)::int END,

    -- Random scripture
    (
        SELECT id
        FROM scriptures
        ORDER BY random()
        LIMIT 1
    ) AS scripture_id,

    -- Random speaker
    (
        SELECT id
        FROM speakers
        ORDER BY random()
        LIMIT 1
    ) AS speaker_id,

    CASE WHEN s.timing = 'past' THEN 'Closing Hymn ' || (random() * 300)::int END,
    CASE WHEN s.timing = 'past' THEN 'Closing prayer offered in gratitude.' END,
    CASE WHEN s.timing = 'past' THEN 'Postlude Hymn ' || (random() * 300)::int END,

    -- Always exactly one hour later
    s.start_time + interval '1 hour' AS date_ended,

    CASE
        WHEN s.timing = 'past' THEN
            'A Tuesday devotional focused on faith, discipleship, and living the gospel of Jesus Christ.'
    END AS summary,

    CASE
        WHEN s.timing = 'past' THEN
            'The speaker shared insights from scripture, bore testimony of Jesus Christ, '
            || 'and encouraged faithful living through prayer, service, and obedience.'
    END AS transcript

FROM series s;

-- =====================================================
-- GENERATE DEVOTIONAL_TOPICS
-- =====================================================

INSERT INTO devotional_topics (devotional_id, topic_id)
SELECT
    d.id AS devotional_id,
    t.id AS topic_id
FROM devotionals d
JOIN LATERAL (
    SELECT id
    FROM topics
    ORDER BY random()
    LIMIT (1 + floor(random() * 10))::int
) t ON true
WHERE d.date_started < now();

-- ====================================================
-- GENERATE USERS
-- =====================================================

DO
$$
DECLARE
    i INT := 0;
    uid UUID;
    padded_index TEXT;
BEGIN
    WHILE i < 1000 LOOP
        -- Pad index to 4 digits
        padded_index := lpad(i::text, 4, '0');

        -- Deterministic UUID:
        -- 00000000-0000-0000-0000-00000000XXXX
        uid := (
            '00000000-0000-0000-0000-00000000' || padded_index
        )::uuid;

        INSERT INTO auth.users (
            id,
            aud,
            role,
            created_at,
            updated_at,
            instance_id,
            is_anonymous
        )
        VALUES (
            uid,
            'authenticated',
            'authenticated',
            now(),
            now(),
            '00000000-0000-0000-0000-000000000000',
            true
        )
        ON CONFLICT (id) DO NOTHING;

        i := i + 1;
    END LOOP;
END;
$$;

-- =====================================================
-- GENERATE TAPS
-- =====================================================

INSERT INTO taps (anon_user_id, devotional_id, stamp)
SELECT
    -- Deterministic anonymous user UUID (0000–0999)
    (
        '00000000-0000-0000-0000-00000000' ||
        lpad((floor(random() * 1000))::int::text, 4, '0')
    )::uuid AS anon_user_id,

    d.id AS devotional_id,

    -- Random timestamp strictly within devotional window
    d.date_started
        + (random() * (d.date_ended - d.date_started)) AS stamp
FROM devotionals d
JOIN LATERAL generate_series(1, 5000) gs ON true
WHERE
    d.date_started < now()
    AND d.date_ended IS NOT NULL;

-- =====================================================
-- GENERATE QUOTES
-- =====================================================

INSERT INTO quotes (
    devotional_id,
    like_count,
    tap_count,
    blob
)
SELECT
    d.id AS devotional_id,

    -- Likes: 0–500
    floor(random() * 500)::int AS like_count,

    -- Taps: likes + extra engagement
    floor(random() * 800)::int AS tap_count,

    -- Quote text
    'Quote #' || q.seq || ' from devotional ' || d.id || ': '
    || CASE (q.seq % 5)
        WHEN 0 THEN 'Faith precedes the miracle.'
        WHEN 1 THEN 'Small and simple things bring about great purposes.'
        WHEN 2 THEN 'The Savior knows you personally.'
        WHEN 3 THEN 'Obedience brings lasting peace.'
        ELSE 'The Lord qualifies those He calls.'
    END AS blob
FROM devotionals d
JOIN LATERAL (
    SELECT generate_series(
        1,
        5 + floor(random() * 11)::int   -- 5–15 quotes
    ) AS seq
) q ON true
WHERE
    d.date_started < now()
    AND d.date_ended IS NOT NULL;

-- =====================================================
-- GENERATE LOG EVENTS
-- =====================================================

-- TODO: Add log event generation if needed in the future

-- =====================================================
-- GENERATE LOG ERRORS
-- =====================================================

-- TODO: Add log error generation if needed in the future
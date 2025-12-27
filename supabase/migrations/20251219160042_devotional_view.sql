CREATE OR REPLACE VIEW devotional_list_view
WITH (security_invoker) AS
SELECT
    d.id,
    d.date_started AS "startDate",
    d.date_ended   AS "endDate",
    d.title,
    d.summary,
    d.transcript,
    d.url,

    CASE WHEN pre_perf.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'title', p_pre.title,
            'composer', p_pre.composer,
            'arranger', p_pre.arranger,
            'performer', jsonb_build_object(
                'firstName', pp.first_name,
                'middleInitial', pp.middle_initial,
                'lastName', pp.last_name
            )
        )
    END AS prelude,

    CASE WHEN pi.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'firstName', pi.first_name,
            'middleInitial', pi.middle_initial,
            'lastName', pi.last_name
        )
    END AS invocation,

    CASE WHEN int_perf.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'title', p_int.title,
            'composer', p_int.composer,
            'arranger', p_int.arranger,
            'performer', jsonb_build_object(
                'firstName', piu.first_name,
                'middleInitial', piu.middle_initial,
                'lastName', piu.last_name
            )
        )
    END AS introit,

    CASE WHEN s.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'reader', jsonb_build_object(
                'firstName', sr.first_name,
                'middleInitial', sr.middle_initial,
                'lastName', sr.last_name
            ),
            'book', s.book,
            'chapter', s.chapter,
            'verses', s.verses,
            'url', s.url
        )
    END AS scripture,

    jsonb_build_object(
        'firstName', sp.first_name,
        'middleInitial', sp.middle_initial,
        'lastName', sp.last_name
    ) AS speaker,

    CASE WHEN post_perf.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'title', p_post.title,
            'composer', p_post.composer,
            'arranger', p_post.arranger,
            'performer', jsonb_build_object(
                'firstName', ppo.first_name,
                'middleInitial', ppo.middle_initial,
                'lastName', ppo.last_name
            )
        )
    END AS postlude,

    CASE WHEN pb.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'firstName', pb.first_name,
            'middleInitial', pb.middle_initial,
            'lastName', pb.last_name
        )
    END AS benediction,

    CASE WHEN rec_perf.id IS NULL THEN NULL ELSE
        jsonb_build_object(
            'title', p_rec.title,
            'composer', p_rec.composer,
            'arranger', p_rec.arranger,
            'performer', jsonb_build_object(
                'firstName', pr.first_name,
                'middleInitial', pr.middle_initial,
                'lastName', pr.last_name
            )
        )
    END AS recessional,

    COALESCE(
        jsonb_agg(DISTINCT t.name)
            FILTER (WHERE t.id IS NOT NULL),
        '[]'::jsonb
    ) AS topics,

    COALESCE(
        jsonb_agg(
            DISTINCT jsonb_build_object(
                'id', q.id,
                'devotionalId', q.devotional_id,
                'likes', q.like_count,
                'taps', q.tap_count,
                'text', q.blob
            )
        ) FILTER (WHERE q.id IS NOT NULL),
        '[]'::jsonb
    ) AS quotes

FROM devotionals d

/* Performances */
LEFT JOIN performances pre_perf  ON pre_perf.id  = d.prelude_id
LEFT JOIN music        p_pre     ON p_pre.id     = pre_perf.music_id
LEFT JOIN people       pp        ON pp.id        = pre_perf.performer_id

LEFT JOIN performances int_perf  ON int_perf.id  = d.introit_id
LEFT JOIN music        p_int     ON p_int.id     = int_perf.music_id
LEFT JOIN people       piu       ON piu.id       = int_perf.performer_id

LEFT JOIN performances post_perf ON post_perf.id = d.postlude_id
LEFT JOIN music        p_post    ON p_post.id    = post_perf.music_id
LEFT JOIN people       ppo       ON ppo.id       = post_perf.performer_id

LEFT JOIN performances rec_perf  ON rec_perf.id  = d.recessional_id
LEFT JOIN music        p_rec     ON p_rec.id     = rec_perf.music_id
LEFT JOIN people       pr        ON pr.id        = rec_perf.performer_id

/* People */
LEFT JOIN people pi ON pi.id = d.invocation_id
LEFT JOIN people sp ON sp.id = d.speaker_id
LEFT JOIN people pb ON pb.id = d.benediction_id

/* Scripture */
LEFT JOIN scriptures s ON s.id = d.scripture_id
LEFT JOIN people sr ON sr.id = s.reader_id

/* Topics */
LEFT JOIN devotional_topics dt ON dt.devotional_id = d.id
LEFT JOIN topics t ON t.id = dt.topic_id

/* Quotes */
LEFT JOIN quotes q ON q.devotional_id = d.id

GROUP BY
    d.id, d.date_started, d.date_ended, d.title, d.summary, d.transcript, d.url,
    pre_perf.id, p_pre.title, p_pre.composer, p_pre.arranger,
    pp.first_name, pp.middle_initial, pp.last_name,
    int_perf.id, p_int.title, p_int.composer, p_int.arranger,
    piu.first_name, piu.middle_initial, piu.last_name,
    post_perf.id, p_post.title, p_post.composer, p_post.arranger,
    ppo.first_name, ppo.middle_initial, ppo.last_name,
    rec_perf.id, p_rec.title, p_rec.composer, p_rec.arranger,
    pr.first_name, pr.middle_initial, pr.last_name,
    pi.id, pi.first_name, pi.middle_initial, pi.last_name,
    sp.first_name, sp.middle_initial, sp.last_name,
    pb.id, pb.first_name, pb.middle_initial, pb.last_name,
    s.id, s.book, s.chapter, s.verses, s.url,
    sr.first_name, sr.middle_initial, sr.last_name;
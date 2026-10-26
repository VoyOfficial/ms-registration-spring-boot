-- Bootstrap default recommended places (Gramado/Canela) so remote/mobile
-- integration can validate GET /v1/places/recommendations without manual inserts.
-- Idempotent: skips rows that already exist by google_place_id.
-- Recommendation window: start = CURRENT_DATE, end = CURRENT_DATE + 1 year.

ALTER TABLE registration.place
    ADD COLUMN IF NOT EXISTS google_place_id VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        JOIN pg_namespace n ON t.relnamespace = n.oid
        WHERE n.nspname = 'registration'
          AND t.relname = 'place'
          AND c.conname = 'uk_place_google_place_id'
    ) THEN
        ALTER TABLE registration.place
            ADD CONSTRAINT uk_place_google_place_id UNIQUE (google_place_id);
    END IF;
EXCEPTION
    WHEN unique_violation THEN
        -- Existing duplicates: leave without unique constraint; inserts still use NOT EXISTS.
        RAISE NOTICE 'Skipping uk_place_google_place_id: duplicate google_place_id values exist';
END $$;

INSERT INTO registration.place (
    name, google_place_id, about, contact, address, city, state,
    rating, userratingstotal, principal_photo, latitude, longitude,
    ranking, start_recommendation, end_recommendation, created_date,
    status, distanceoflocal, issaved
)
SELECT v.name, v.google_place_id, v.about, v.contact, v.address, v.city, v.state,
       v.rating, v.userratingstotal, v.principal_photo, v.latitude, v.longitude,
       v.ranking, v.start_recommendation, v.end_recommendation, v.created_date,
       v.status, v.distanceoflocal, v.issaved
FROM (
    VALUES
        ('Fonte do Amor Eterno', 'ChIJsdUuyRc1GZURg7Hy1kfaAeU', '', '(54) 3286-1055',
         'Av. Borges de Medeiros, 2659 - Centro, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.37855::double precision, -50.8744::double precision,
         1, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Portico Gramado Estilo Bavaro', 'ChIJkT12uPczGZURcExGzF48AoI', '', '(54) 3036-4050',
         'Av. das Hortensias - Portico, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.38969::double precision, -50.88495::double precision,
         2, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Mini Mundo', 'ChIJoW5_xxs2GZURGyI9uVrtAeY', '', '(54) 3286-4055',
         'R. Horacio Cardoso, 291 - Planalto, Gramado - RS, 95675-062', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.38304::double precision, -50.87568::double precision,
         3, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Garden Park Gramado', 'ChIJWzOwRXM1GZURgCqEqfAbAiE', '', '(54) 3286-4051',
         'Estr. Profa. Elvira Apolo Benetti, 1699 - Jardim Bela Vista, Gramado - RS, 95679-899',
         'Gramado', 'RS',
         4.5::real, 1000, '', -29.37264::double precision, -50.85285::double precision,
         4, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Lago Negro', 'ChIJpQLdRlMzGZURHGysIJMbi3I', '', '(54) 3286-4052',
         'R. Vinte e Cinco de Julho, 439 - Casa Grande, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.39351::double precision, -50.87833::double precision,
         5, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Expogramado', 'ChIJIYpuLR8xGZURIDT2A5S6Vog', '', '(54) 3286-4053',
         'Av. Borges de Medeiros, 4111 - Centro, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.36883::double precision, -50.88178::double precision,
         1, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Belvedere Vale do Quilombo', 'ChIJ-ZdZYBc1GZURaaRRs7WmIAg', '', '(54) 3286-4054',
         'Av. das Hortensias, 2536 - Vila Suica, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.37812::double precision, -50.86689::double precision,
         2, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('NBA Park Gramado', 'ChIJc9xQaBk1GZURAzZGz7paAeQ', '', '(54) 3286-4056',
         'Av. das Hortensias, 4795 - Carniel, Gramado - RS, 95670-880', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.35966::double precision, -50.85485::double precision,
         3, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Jardim do Amor', 'ChIJYZJhix41GZURPZQ1hcAfIIo', '', '(54) 3286-4057',
         'Av. das Hortensias, 765 - Centro, Gramado - RS, 95670-000', 'Gramado', 'RS',
         4.5::real, 1000, '', -29.38436::double precision, -50.8815::double precision,
         4, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Chocolate Gramadense', 'ChIJCXYmbCY1GZURMMaJctqNAgQ', '', '(54) 3286-4058',
         'R. Pref. Waldemar Frederico Weber, 365 - Floresta, Gramado - RS, 95670-000',
         'Gramado', 'RS',
         4.5::real, 1000, '', -29.37138::double precision, -50.88661::double precision,
         5, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE),
        ('Space Adventure Canela', 'ChIJSpaceAdvCanelaTest01', '', '(54) 3286-1055',
         'Av. Ernani Kroeff Fleck, 960 - Vila Suica, Canela - RS, 95684-180', 'Canela', 'RS',
         4.5::real, 1000, '', -29.35963::double precision, -50.83543::double precision,
         1, CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 year')::date, CURRENT_DATE,
         TRUE, 0::real, FALSE)
) AS v(
    name, google_place_id, about, contact, address, city, state,
    rating, userratingstotal, principal_photo, latitude, longitude,
    ranking, start_recommendation, end_recommendation, created_date,
    status, distanceoflocal, issaved
)
WHERE NOT EXISTS (
    SELECT 1
    FROM registration.place p
    WHERE p.google_place_id = v.google_place_id
);

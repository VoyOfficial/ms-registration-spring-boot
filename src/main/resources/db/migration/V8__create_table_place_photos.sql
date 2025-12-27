CREATE TABLE IF NOT EXISTS registration.place_photos (
    id BIGSERIAL PRIMARY KEY,
    place_id BIGINT NOT NULL REFERENCES registration.place(id) ON DELETE CASCADE,
    photo_reference TEXT,
    image_base64 BYTEA, -- Ou BYTEA se preferir armazenar bytes
    height INTEGER,
    width INTEGER,
    html_attributions TEXT
);
CREATE TABLE IF NOT EXISTS registration.sponsor (
    id                  BIGSERIAL       NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    city                VARCHAR(255)    NOT NULL,
    name_key            VARCHAR(255)    NOT NULL,
    city_key            VARCHAR(255)    NOT NULL,
    approved            BOOLEAN         NOT NULL DEFAULT FALSE,
    subscription_active BOOLEAN         NOT NULL DEFAULT FALSE,
    ranking             INTEGER,
    google_place_id     VARCHAR(255),
    notes               VARCHAR(500),
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_sponsor_name_city UNIQUE (name_key, city_key)
);

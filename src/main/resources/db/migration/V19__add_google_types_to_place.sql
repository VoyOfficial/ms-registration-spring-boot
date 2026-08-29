ALTER TABLE registration.place
    ADD COLUMN IF NOT EXISTS google_types varchar(500);

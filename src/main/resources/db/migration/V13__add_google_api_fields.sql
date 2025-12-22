-- Add missing fields from Google Places API that are in PlaceModel but not in database
ALTER TABLE registration.place
    ADD COLUMN IF NOT EXISTS about TEXT,
    ADD COLUMN IF NOT EXISTS rating REAL,
    ADD COLUMN IF NOT EXISTS userRatingsTotal INTEGER,
    ADD COLUMN IF NOT EXISTS isSaved BOOLEAN DEFAULT false;


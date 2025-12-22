-- Add missing columns to place table for Google Places API data
-- Only add columns that don't exist yet
ALTER TABLE registration.place
    ADD COLUMN IF NOT EXISTS principal_photo TEXT,
    ADD COLUMN IF NOT EXISTS principal_photo_url TEXT;

-- Note: google_place_id was already added, so we skip it to avoid warnings

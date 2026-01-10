-- Add photo_url column to place_photos table
ALTER TABLE registration.place_photos ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);


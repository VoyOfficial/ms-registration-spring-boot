-- Increase photo_url column size to handle longer Google API URLs
ALTER TABLE registration.place_photos ALTER COLUMN photo_url TYPE TEXT;


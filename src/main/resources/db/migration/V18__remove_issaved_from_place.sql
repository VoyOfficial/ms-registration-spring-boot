-- Remove isSaved column from place table as it's now handled by user_saved_places join table
ALTER TABLE registration.place DROP COLUMN IF EXISTS isSaved;


-- Make the state column nullable since Google Places API doesn't always provide state information
ALTER TABLE registration.place ALTER COLUMN state DROP NOT NULL;


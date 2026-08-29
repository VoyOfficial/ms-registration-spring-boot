-- Make issaved and distanceoflocal columns nullable since Google Places API doesn't provide these fields
-- issaved is a business logic field that should default to false
-- distanceoflocal is calculated later and may not be available initially

ALTER TABLE registration.place ALTER COLUMN issaved DROP NOT NULL;
ALTER TABLE registration.place ALTER COLUMN distanceoflocal DROP NOT NULL;


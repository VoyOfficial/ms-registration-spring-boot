-- Make several columns nullable since Google Places API doesn't always provide these fields
-- or the PlaceModel no longer uses them
--
-- city: embedded in the address/vicinity field, not provided separately in Nearby Search
-- contact: only available in Place Details API, not in Nearby Search
-- about: Google API doesn't always provide this, set to empty string in code
-- rating: may not be available for new places
-- userRatingsTotal: may not be available for new places
-- business_hours_id: no longer used in PlaceModel, will be removed in future migration
-- document: no longer used in PlaceModel

-- Make city nullable if it exists and is NOT NULL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'city'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN city DROP NOT NULL;
    END IF;
END $$;

-- Make contact nullable if it exists and is NOT NULL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'contact'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN contact DROP NOT NULL;
    END IF;
END $$;

-- Make about nullable if it still has NOT NULL constraint
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'about'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN about DROP NOT NULL;
    END IF;
END $$;

-- Drop foreign key constraint and make business_hours_id nullable
-- First, find and drop the foreign key constraint
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    -- Find the foreign key constraint name
    SELECT tc.constraint_name INTO constraint_name
    FROM information_schema.table_constraints AS tc
    WHERE tc.table_schema = 'registration'
      AND tc.table_name = 'place'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND tc.constraint_name LIKE '%business_hours%';

    -- Drop the constraint if found
    IF constraint_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE registration.place DROP CONSTRAINT ' || constraint_name;
    END IF;
END $$;

-- Make business_hours_id nullable if it exists and is NOT NULL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'business_hours_id'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN business_hours_id DROP NOT NULL;
    END IF;
END $$;

-- Make document nullable (no longer used in PlaceModel) - only if column exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'document'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN document DROP NOT NULL;
    END IF;
END $$;

-- Make rating nullable (may not be available for new places) - only if NOT NULL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'rating'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN rating DROP NOT NULL;
    END IF;
END $$;

-- Make userRatingsTotal nullable (may not be available for new places) - only if NOT NULL
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'registration'
          AND table_name = 'place'
          AND column_name = 'userratingstotal'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE registration.place ALTER COLUMN userratingstotal DROP NOT NULL;
    END IF;
END $$;


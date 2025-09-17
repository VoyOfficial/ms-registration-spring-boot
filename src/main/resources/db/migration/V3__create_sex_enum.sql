DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'sex_enum' AND typnamespace = 'registration'::regnamespace) THEN
        CREATE TYPE registration.sex_enum AS ENUM ('MALE', 'FEMALE', 'DO_NOT_INFORM');
    END IF;
END $$;
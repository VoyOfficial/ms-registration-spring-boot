DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'marital_status_enum' AND typnamespace = 'registration'::regnamespace) THEN
        CREATE TYPE registration.marital_status_enum AS ENUM ('SINGLE', 'MARRIED', 'DIVORCED', 'SEPARATED', 'WIDOWED');
    END IF;
END $$;
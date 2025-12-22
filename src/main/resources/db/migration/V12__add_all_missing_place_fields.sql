-- Verificar e adicionar todos os campos necessários para o PlaceModel
-- Baseado na estrutura da V7 e nas necessidades do Google Places API

-- Verificar se as colunas existem antes de adicionar
DO $$
BEGIN
    -- Adicionar principal_photo se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='principal_photo') THEN
        ALTER TABLE registration.place ADD COLUMN principal_photo TEXT;
    END IF;

    -- Adicionar principal_photo_url se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='principal_photo_url') THEN
        ALTER TABLE registration.place ADD COLUMN principal_photo_url TEXT;
    END IF;

    -- Adicionar latitude se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='latitude') THEN
        ALTER TABLE registration.place ADD COLUMN latitude DOUBLE PRECISION DEFAULT 0.0;
    END IF;

    -- Adicionar longitude se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='longitude') THEN
        ALTER TABLE registration.place ADD COLUMN longitude DOUBLE PRECISION DEFAULT 0.0;
    END IF;

    -- Adicionar ranking se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='ranking') THEN
        ALTER TABLE registration.place ADD COLUMN ranking INTEGER;
    END IF;

    -- Adicionar start_recommendation se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='start_recommendation') THEN
        ALTER TABLE registration.place ADD COLUMN start_recommendation DATE;
    END IF;

    -- Adicionar end_recommendation se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='end_recommendation') THEN
        ALTER TABLE registration.place ADD COLUMN end_recommendation DATE;
    END IF;

    -- Adicionar created_date se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='created_date') THEN
        ALTER TABLE registration.place ADD COLUMN created_date DATE;
    END IF;

    -- Adicionar last_cancel se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='last_cancel') THEN
        ALTER TABLE registration.place ADD COLUMN last_cancel DATE;
    END IF;

    -- Adicionar status se não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='registration' AND table_name='place' AND column_name='status') THEN
        ALTER TABLE registration.place ADD COLUMN status BOOLEAN DEFAULT false;
    END IF;
END $$;


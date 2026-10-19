# Sponsor spreadsheet import (MVP)

Ops keeps the eligible establishments in a spreadsheet and loads `registration.sponsor`. The API does not read Excel at runtime.

`POST /v1/places` looks up `name` + `city`. The row is eligible only when `approved` and `subscription_active` are both true. Anything else, including a missing row, returns 403.

## Normalization

The lookup key trims the value, collapses internal whitespace, and lowercases it. Accents are kept, so `São Paulo` does not match `Sao Paulo`. The request city sent to Google Places is still compared exactly with the Google address. Only the sponsor lookup is case-insensitive.

## CSV

Header:

```text
name,city,approved,subscription_active,ranking,google_place_id,notes
```

`approved` and `subscription_active` accept `true`/`false` (also `yes`/`no`, `sim`/`nao`, `1`/`0`). `ranking` and `google_place_id` may be empty. See `scripts/sponsors.example.csv`.

## Load

```bash
python3 scripts/import-sponsors.py sponsors.csv > sponsors.sql
psql "$DATABASE_URL" -f sponsors.sql
```

The script writes an idempotent upsert on `(name_key, city_key)`. Importing the same establishment again updates approval, subscription, ranking, Google place id, and notes.

Apply Flyway `V20__create_table_sponsor.sql` before the first import.

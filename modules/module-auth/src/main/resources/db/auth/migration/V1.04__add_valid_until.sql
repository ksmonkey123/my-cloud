-- Step 1: Add the column as nullable
ALTER TABLE auth.auth_token
    ADD COLUMN valid_until TIMESTAMP;

-- Step 2: Initialize it with cre_time + 4 hours
UPDATE auth.auth_token
SET valid_until = cre_time + INTERVAL '4 hours';

-- Step 3: Enforce NOT NULL constraint
ALTER TABLE auth.auth_token
    ALTER COLUMN valid_until SET NOT NULL;
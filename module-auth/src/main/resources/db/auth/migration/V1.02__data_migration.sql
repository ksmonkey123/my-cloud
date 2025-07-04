CREATE OR REPLACE FUNCTION copy_table_if_exists(
    old_table_name text,
    new_table_name text
) RETURNS void AS $$
DECLARE
    src_exists boolean;
BEGIN
    -- Check if the table exists in source schema
    SELECT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'core'
          AND table_name = old_table_name
    ) INTO src_exists;

    IF src_exists THEN
        -- Copy data from source to destination
        EXECUTE format(
                'INSERT INTO %I.%I SELECT * FROM %I.%I',
                'auth', new_table_name, 'core', old_table_name
                );

        RAISE NOTICE 'Copied data from core.% to auth.%', old_table_name, new_table_name;
    ELSE
        RAISE NOTICE 'Table core.% does not exist. Skipping.', old_table_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

delete from auth_token;
delete from account_role;
delete from role;
delete from account;

SELECT copy_table_if_exists('auth_account','account');
SELECT copy_table_if_exists('auth_token','auth_token');
SELECT copy_table_if_exists('auth_role','role');
SELECT copy_table_if_exists('auth_account_role','account_role');

DROP FUNCTION copy_table_if_exists(old_table_name text, new_table_name text);
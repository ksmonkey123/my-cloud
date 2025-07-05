CREATE OR REPLACE FUNCTION copy_table_if_exists(
    _table_name text
) RETURNS void AS $$
DECLARE
    src_exists boolean;
BEGIN
    -- Check if the table exists in source schema
    SELECT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'canary'
          AND table_name = _table_name
    ) INTO src_exists;

    IF src_exists THEN
        -- Copy data from source to destination
        EXECUTE format(
                'INSERT INTO %I.%I SELECT * FROM %I.%I',
                'docker', _table_name, 'canary', _table_name
                );

        RAISE NOTICE 'Copied data from canary.% to docker.%', _table_name, _table_name;
    ELSE
        RAISE NOTICE 'Table docker.% does not exist. Skipping.', _table_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

SELECT copy_table_if_exists('docker_monitored_entry');
SELECT copy_table_if_exists('docker_entry_state');

DROP FUNCTION copy_table_if_exists(_table_name text);
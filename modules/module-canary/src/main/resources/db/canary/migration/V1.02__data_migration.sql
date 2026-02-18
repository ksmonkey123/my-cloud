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
                'canary', new_table_name, 'core', old_table_name
                );

        RAISE NOTICE 'Copied data from core.% to canary.%', old_table_name, new_table_name;
    ELSE
        RAISE NOTICE 'Table core.% does not exist. Skipping.', old_table_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

delete from docker_entry_state;
delete from docker_monitored_entry;
delete from web_failed_test;
delete from web_test_record;
delete from web_site_test;
delete from web_monitored_site;

SELECT copy_table_if_exists('canary_docker_monitored_entry','docker_monitored_entry');
SELECT copy_table_if_exists('canary_docker_entry_state','docker_entry_state');
SELECT copy_table_if_exists('canary_web_monitored_site','web_monitored_site');
SELECT copy_table_if_exists('canary_web_site_test','web_site_test');
SELECT copy_table_if_exists('canary_web_test_record','web_test_record');
SELECT copy_table_if_exists('canary_web_failed_test','web_failed_test');

DROP FUNCTION copy_table_if_exists(old_table_name text, new_table_name text);
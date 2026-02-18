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
                'bookkeeping', new_table_name, 'core', old_table_name
                );

        RAISE NOTICE 'Copied data from core.% to bookkeeping.%', old_table_name, new_table_name;
    ELSE
        RAISE NOTICE 'Table core.% does not exist. Skipping.', old_table_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

delete from booking_movement;
delete from booking_record;
delete from account;
delete from account_group;
delete from book;

SELECT copy_table_if_exists('bookkeeping_book','book');
SELECT copy_table_if_exists('bookkeeping_account_group','account_group');
SELECT copy_table_if_exists('bookkeeping_account','account');
SELECT copy_table_if_exists('bookkeeping_booking_record','booking_record');
SELECT copy_table_if_exists('bookkeeping_booking_movement','booking_movement');

DROP FUNCTION copy_table_if_exists(old_table_name text, new_table_name text);
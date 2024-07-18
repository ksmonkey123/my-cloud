alter table failed_test
    add
        constraint fk_failed_test__record_id foreign key (record_id) references test_record (id);
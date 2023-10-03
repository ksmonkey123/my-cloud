create sequence test_record_seq start 1 increment 50;

create table test_record
(
    -- id columns
    id            bigint     not null,
    site_id       bigint     not null,
    -- business columns
    result        varchar(7) not null,
    error_message text,
    -- management columns
    cre_user      varchar(30),
    mut_user      varchar(30),
    cre_time      timestamp  not null default current_timestamp,
    mut_time      timestamp  not null default current_timestamp,
    version       int        not null default 0,
    -- constraints
    constraint pk_test_record primary key (id),
    constraint fk_test_record__site_id foreign key (site_id) references monitored_site (id)
);

create index idx_test_record__site_id__cre_time on test_record (site_id, cre_time);

create table failed_test
(
    record_id   bigint not null,
    test_string text   not null,
    -- constraints
    constraint pk_failed_test primary key (record_id, test_string)
);

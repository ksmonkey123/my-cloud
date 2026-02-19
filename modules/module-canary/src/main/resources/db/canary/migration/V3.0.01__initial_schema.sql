create table web_monitored_site
(
    -- id columns
    id       bigint      not null,
    owner    varchar(30) not null,
    -- business columns
    site_url text        not null,
    enabled  boolean     not null,
    -- management columns
    version  int         not null default 0,
    -- constraints
    constraint pk_web_monitored_site primary key (id)
);

create index idx_web_monitored_site__owner on web_monitored_site (owner);

create table web_site_test
(
    -- id columns
    site_id     bigint  not null,
    -- business columns
    test_string text    not null,
    enabled     boolean not null,
    -- constraints
    constraint uq_web_site_test__test_string_per_site unique (site_id, test_string),
    constraint fk_web_site_test__site_id foreign key (site_id) references web_monitored_site (id)
);

create table web_test_record
(
    -- id columns
    id            bigint     not null,
    site_id       bigint     not null,
    -- business columns
    result        varchar(7) not null,
    error_message text,
    recorded_at   timestamp  not null default current_timestamp,
    -- management columns
    version       int        not null default 0,
    -- constraints
    constraint pk_web_test_record primary key (id),
    constraint fk_web_test_record__site_id foreign key (site_id) references web_monitored_site (id)
);

create index idx_web_test_record__site_id__cre_time on web_test_record (site_id, recorded_at);

create table web_failed_test
(
    record_id   bigint not null,
    test_string text   not null,
    -- constraints
    constraint pk_web_failed_test primary key (record_id, test_string),
    constraint fk_web_failed_test__record_id foreign key (record_id) references web_test_record (id)
);

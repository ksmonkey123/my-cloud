create sequence monitored_site_seq start 1 increment 50;

create table monitored_site
(
    -- id columns
    id       bigint    not null,
    -- business columns
    site_url text      not null,
    enabled  boolean   not null,
    -- management columns
    cre_user varchar(30),
    mut_user varchar(30),
    cre_time timestamp not null default current_timestamp,
    mut_time timestamp not null default current_timestamp,
    version  int       not null default 0,
    -- constraints
    constraint pk_monitored_site primary key (id)
);

create table site_test
(
    -- id columns
    site_id     bigint    not null,
    -- business columns
    test_string text      not null,
    enabled     boolean   not null,
    -- constraints
    constraint uq_site_test__test_string_per_site unique (site_id, test_string),
    constraint fk_site_test__site_id foreign key (site_id) references monitored_site (id)
);
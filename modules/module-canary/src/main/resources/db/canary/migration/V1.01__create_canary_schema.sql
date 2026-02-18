create table web_monitored_site
(
    -- id columns
    id       bigint      not null,
    -- business columns
    site_url text        not null,
    enabled  boolean     not null,
    -- management columns
    cre_user varchar(30) not null,
    mut_user varchar(30) not null,
    cre_time timestamp   not null default current_timestamp,
    mut_time timestamp   not null default current_timestamp,
    version  int         not null default 0,
    -- constraints
    constraint pk_web_monitored_site primary key (id)
);

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
    id            bigint      not null,
    site_id       bigint      not null,
    -- business columns
    result        varchar(7)  not null,
    error_message text,
    -- management columns
    cre_user      varchar(30) not null,
    mut_user      varchar(30) not null,
    cre_time      timestamp   not null default current_timestamp,
    mut_time      timestamp   not null default current_timestamp,
    version       int         not null default 0,
    -- constraints
    constraint pk_web_test_record primary key (id),
    constraint fk_web_test_record__site_id foreign key (site_id) references web_monitored_site (id)
);

create index idx_web_test_record__site_id__cre_time on web_test_record (site_id, cre_time);

create table web_failed_test
(
    record_id   bigint not null,
    test_string text   not null,
    -- constraints
    constraint pk_web_failed_test primary key (record_id, test_string),
    constraint fk_web_failed_test__record_id foreign key (record_id) references web_test_record (id)
);

create table docker_monitored_entry
(
    -- id columns
    id               bigint       not null,
    -- business columns
    namespace        varchar(100),
    repository       varchar(100) not null,
    tag              varchar(100) not null,
    tag_changes_only boolean      not null,
    enabled          boolean      not null,
    -- management columns
    cre_user         varchar(30)  not null,
    mut_user         varchar(30)  not null,
    cre_time         timestamp    not null default current_timestamp,
    mut_time         timestamp    not null default current_timestamp,
    version          int          not null default 0,
    -- constraints
    constraint pk_docker_monitored_entry primary key (id),
    constraint uq_docker_monitored_entry unique nulls not distinct (namespace, repository, tag)
);

create table docker_entry_state
(
    -- id columns
    id                 bigint       not null,
    monitored_entry_id bigint       not null,
    -- business columns
    digest             varchar(100) not null,
    tags               text         not null,
    -- management columns
    cre_user           varchar(30)  not null,
    mut_user           varchar(30)  not null,
    cre_time           timestamp    not null default current_timestamp,
    mut_time           timestamp    not null default current_timestamp,
    version            int          not null default 0,
    -- constraints
    constraint pk_docker_entry_state primary key (id),
    constraint fk_docker_entry_state__monitored_entry_id foreign key (monitored_entry_id) references docker_monitored_entry (id)
);


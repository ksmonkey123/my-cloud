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
    -- columns added later on (order relevant for migration)
    owner            varchar(30)  not null,
    -- constraints
    constraint pk_docker_monitored_entry primary key (id),
    constraint uq_docker_monitored_entry unique nulls not distinct (namespace, repository, tag)
);

create index idx_docker_monitored_entry__owner on docker_monitored_entry (owner);

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


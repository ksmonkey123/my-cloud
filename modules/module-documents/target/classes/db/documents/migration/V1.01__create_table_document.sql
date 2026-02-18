create table document
(
    -- id columns
    id          bigint      not null,
    token       varchar(43) not null,
    -- business columns
    filename    varchar(50) not null,
    type        varchar(50) not null,
    valid_until timestamp   not null,
    content     bytea       not null,
    -- management fields
    cre_user    varchar(30) not null,
    mut_user    varchar(30) not null,
    cre_time    timestamp   not null default current_timestamp,
    mut_time    timestamp   not null default current_timestamp,
    version     int         not null default 0,
    -- constraints
    constraint pk_document primary key (id),
    constraint uq_document_token unique (token)
);

create index idx_document__valid_until on document (valid_until);

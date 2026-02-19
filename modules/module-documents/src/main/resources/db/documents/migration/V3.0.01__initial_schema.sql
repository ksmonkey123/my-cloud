create table document
(
    -- id columns
    id          bigint      not null,
    token       varchar(22) not null,
    -- business columns
    username    varchar(30) not null,
    source      text        not null,
    filename    varchar(50) not null,
    type        varchar(50) not null,
    created_at  timestamp   not null default current_timestamp,
    valid_until timestamp   not null,
    content     bytea       not null,
    -- management fields
    version     int         not null default 0,
    -- constraints
    constraint pk_document primary key (id),
    constraint uq_document_token unique (token),
    constraint ck_document_source check (source in ('BOOKKEEPING'))
);

create index idx_document__valid_until on document (valid_until);
create index idx_document__username on document (username, created_at);

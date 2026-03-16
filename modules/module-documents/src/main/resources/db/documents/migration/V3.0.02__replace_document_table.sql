drop table document;

create table document
(
    -- id columns
    id          uuid        not null,
    -- business columns
    username    varchar(30) not null,
    source      text        not null,
    filename    varchar(50) not null,
    type        varchar(50) not null,
    created_at  timestamp   not null,
    valid_until timestamp   not null,
    content     bytea       not null,
    -- constraints
    constraint pk_document primary key (id),
    constraint ck_document_source check (source in ('BOOKKEEPING'))
);

create index idx_document__valid_until on document (valid_until);
create index idx_document__username on document (username, created_at);

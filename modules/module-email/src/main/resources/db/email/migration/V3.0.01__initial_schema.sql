create table outbox
(
    -- id columns
    id           bigint       not null,
    message_uid  text,
    -- business columns
    sent         boolean      not null default false,
    recipient    varchar(100) not null,
    subject      varchar(100) not null,
    body_content text         not null,
    body_format  varchar(8)   not null,
    -- management columns
    created_at   timestamp    not null default current_timestamp,
    sent_at      timestamp,
    version      int          not null default 0,
    -- constraints
    constraint pk_outbox primary key (id),
    constraint uq_outbox__message_uid unique (message_uid),
    constraint ck_outbox__body_format check (body_format in ('HTML', 'TEXT', 'MARKDOWN'))
);

create index idx_outbox_unsent_cre_time on outbox (created_at) where not sent;
create table email_outbox
(
    -- id columns
    id           bigint       not null,
    -- business columns
    sent         boolean      not null default false,
    sender       varchar(100) not null,
    recipient    varchar(100) not null,
    subject      varchar(100) not null,
    body_content text         not null,
    body_format  varchar(4)   not null,
    -- management columns
    cre_user     varchar(30)  not null,
    mut_user     varchar(30)  not null,
    cre_time     timestamp    not null default current_timestamp,
    mut_time     timestamp    not null default current_timestamp,
    version      int          not null default 0,
    -- constraints
    constraint pk_email_outbox primary key (id),
    constraint ck_email_outbox__body_format check (body_format in ('HTML', 'TEXT'))
);

create index idx_email_outbox_unsent_cre_time on email_outbox (cre_time) where not sent;
create table audit_log
(
    id         uuid      not null,
    account_id bigint    not null,
    token_id   bigint,
    key_id     bigint,
    method     text      not null,
    path       text      not null,
    created_at timestamp not null,

    constraint pk_audit_log primary key (id),
    constraint fk_audit_log__account_id foreign key (account_id) references account
)
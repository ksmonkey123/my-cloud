create sequence account_seq start 1 increment 50;
create sequence auth_token_seq start 1 increment 50;

create table account
(
    id       bigint      not null,
    username varchar(30) not null,
    password varchar(72) not null,
    enabled  boolean     not null,
    admin    boolean     not null,

    constraint pk_account primary key (id),
    constraint uq_account__username unique (username)
);

create table auth_token
(
    id            bigint      not null,
    token_string  varchar(88) not null,
    account_id    bigint      not null,
    creation_time timestamp   not null,

    constraint pk_auth_token primary key (id),
    constraint uq_auth_token__token_string unique (token_string),
    constraint fk_auth_token__account_id foreign key (account_id) references account
);

create index idx_auth_token__account_id on auth_token (account_id);

create table auth_account
(
    -- key fields
    id       bigint      not null,
    username varchar(30) not null,
    -- business fields
    password varchar(72) not null,
    enabled  boolean     not null,
    admin    boolean     not null,
    language varchar(2)  not null,
    -- management fields
    cre_user varchar(30) not null,
    mut_user varchar(30) not null,
    cre_time timestamp   not null default current_timestamp,
    mut_time timestamp   not null default current_timestamp,
    version  int         not null default 0,

    constraint pk_auth_account primary key (id),
    constraint uq_auth_account__username unique (username)
);

create index idx_auth_account__cre_time on auth_account (cre_time);
create index idx_auth_account__mut_time on auth_account (mut_time);

create table auth_token
(
    -- key fields
    id           bigint      not null,
    token_string varchar(88) not null,
    account_id   bigint      not null,
    -- business fields
    -- management fields
    cre_user     varchar(30) not null,
    mut_user     varchar(30) not null,
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,

    constraint pk_auth_token primary key (id),
    constraint uq_auth_token__token_string unique (token_string),
    constraint fk_auth_token__account_id foreign key (account_id) references auth_account
);

create index idx_auth_token__account_id on auth_token (account_id);
create index idx_auth_token__cre_time on auth_token (cre_time);
create index idx_auth_token__mut_time on auth_token (mut_time);

create table auth_role
(
    -- key fields
    id          bigint      not null,
    name        varchar(20) not null,
    -- business fields
    description varchar(50),
    enabled     boolean     not null,
    -- management fields
    cre_user    varchar(30) not null,
    mut_user    varchar(30) not null,
    cre_time    timestamp   not null default current_timestamp,
    mut_time    timestamp   not null default current_timestamp,
    version     int         not null default 0,

    constraint pk_auth_role primary key (id),
    constraint uq_auth_role__name unique (name)
);

create index idx_auth_role__cre_time on auth_role (cre_time);
create index idx_auth_role__mut_time on auth_role (mut_time);

create table auth_account_role
(
    account_id bigint not null,
    role_id    bigint not null,
    constraint pk_auth_account_role primary key (account_id, role_id),
    constraint fk_auth_account_role__account_id foreign key (account_id) references auth_account (id),
    constraint fk_auth_account_role__role_id foreign key (role_id) references auth_role (id)
);

create index idx_auth_account_role__account_id on auth_account_role (account_id);
create index idx_auth_account_role__role_id on auth_account_role (role_id);
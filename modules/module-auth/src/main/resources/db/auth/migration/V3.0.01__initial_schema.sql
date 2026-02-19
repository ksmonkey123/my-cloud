create table account
(
    -- key fields
    id       bigint      not null,
    username varchar(30) not null,
    -- business fields
    password varchar(72) not null,
    enabled  boolean     not null,
    admin    boolean     not null,
    language varchar(2)  not null,
    email    varchar(100),
    -- management fields
    version  int         not null default 0,

    constraint pk_account primary key (id),
    constraint uq_account__username unique (username)
);

create table auth_token
(
    -- key fields
    id           bigint      not null,
    token_string varchar(43) not null,
    account_id   bigint      not null,
    -- business fields
    valid_until  timestamp   not null,
    -- management fields
    version      int         not null default 0,

    constraint pk_auth_token primary key (id),
    constraint uq_auth_token__token_string unique (token_string),
    constraint fk_auth_token__account_id foreign key (account_id) references account
);

create index idx_auth_token__account_id on auth_token (account_id);

create table role
(
    -- key fields
    id          bigint      not null,
    name        varchar(20) not null,
    -- business fields
    description varchar(50),
    authorities text,
    -- management fields
    version     int         not null default 0,

    constraint pk_role primary key (id),
    constraint uq_role__name unique (name)
);

create table account_role
(
    account_id bigint not null,
    role_id    bigint not null,
    constraint pk_account_role primary key (account_id, role_id),
    constraint fk_account_role__account_id foreign key (account_id) references account (id),
    constraint fk_account_role__role_id foreign key (role_id) references role (id)
);

create index idx_account_role__account_id on account_role (account_id);
create index idx_account_role__role_id on account_role (role_id);

create table api_key
(
    -- key fields
    id            bigint      not null,
    token_string  varchar(86) not null,
    owner_id      bigint      not null,
    -- business fields
    name          varchar(30) not null,
    creation_time timestamp   not null,
    authorities   text,
    -- management fields
    version       int         not null default 0,

    -- constraints
    constraint pk_api_key primary key (id),
    constraint uq_api_key__token_string unique (token_string),
    constraint uq_api_key__name_per_owner unique (owner_id, name),
    constraint fk_api_key__owner_id foreign key (owner_id) references account
);

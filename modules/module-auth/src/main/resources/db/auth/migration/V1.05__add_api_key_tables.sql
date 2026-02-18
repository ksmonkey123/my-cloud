create table api_key
(
    -- key fields
    id            bigint      not null,
    token_string  varchar(86) not null,
    owner_id      bigint      not null,
    -- business fields
    name          varchar(30) not null,
    creation_time timestamp   not null,
    -- management fields
    version       int         not null default 0,

    -- constraints
    constraint pk_api_key primary key (id),
    constraint uq_api_key__token_string unique (token_string),
    constraint uq_api_key__name_per_owner unique (owner_id, name),
    constraint fk_api_key__owner_id foreign key (owner_id) references account
);

create table api_key_role
(
    api_key_id bigint not null,
    role_id    bigint not null,
    constraint pk_api_key_role primary key (api_key_id, role_id),
    constraint fk_api_key_role__api_key_id foreign key (api_key_id) references api_key,
    constraint fk_api_key_role__role_id foreign key (role_id) references role
);

create index idx_api__key_role__role_id on api_key_role (role_id);
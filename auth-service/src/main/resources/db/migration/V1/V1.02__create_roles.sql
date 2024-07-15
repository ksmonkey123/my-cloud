create sequence role_seq start 1 increment 50;

create table role
(
    id          bigint      not null,
    name        varchar(20) not null,
    description varchar(50),
    enabled     boolean     not null,
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

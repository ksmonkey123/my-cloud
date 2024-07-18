create table account
(
    -- id columns
    id               bigint      not null,
    account_group_id bigint      not null,
    -- business columns
    account_type     varchar(1)  not null,
    account_number   int         not null,
    title            varchar(50) not null,
    description      text,
    -- management fields
    cre_user         varchar(30),
    mut_user         varchar(30),
    cre_time         timestamp   not null default current_timestamp,
    mut_time         timestamp   not null default current_timestamp,
    version          int         not null default 0,
    -- constraints
    constraint pk_account primary key (id),
    constraint fk_account__account_group_id foreign key (account_group_id) references account_group (id),
    constraint uq_account__title_per_group unique (account_group_id, title),
    constraint uq_account__number_per_group unique (account_number, title),
    constraint ck_account__account_type check (account_type in ('A', 'P', '+', '-')),
    constraint ck_account__account_number check (account_number > 0 and account_number < 1000)
);

create index idx_account__account_group_id on account (account_group_id);
create index idx_account__cre_time on account (cre_time);
create index idx_account__mut_time on account (mut_time);

create sequence account_seq start 1 increment 50;

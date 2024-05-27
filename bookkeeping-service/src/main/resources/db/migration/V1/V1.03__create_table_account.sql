create table account
(
    -- id columns
    id               bigint        not null,
    book_id          bigint        not null,
    account_group_id bigint,
    -- business columns
    account_type     varchar(1)    not null,
    title            varchar(50)   not null,
    initial_balance  numeric(9, 2) not null,
    -- management fields
    cre_user         varchar(30),
    mut_user         varchar(30),
    cre_time         timestamp     not null default current_timestamp,
    mut_time         timestamp     not null default current_timestamp,
    version          int           not null default 0,
    -- constraints
    constraint pk_account primary key (id),
    constraint fk_account__book_id foreign key (book_id) references book (id),
    constraint fk_account__account_group_id foreign key (account_group_id) references account_group (id),
    constraint uq_account__title_per_book unique (book_id, title),
    constraint ck_account__account_type check (account_type in ('A', 'P', '+', '-'))
);

create index idx_account__book_id on account (book_id);
create index idx_account__account_group_id on account (account_group_id);
create index idx_account__cre_time on account (cre_time);
create index idx_account__mut_time on account (mut_time);

create sequence account_seq start 1 increment 50;

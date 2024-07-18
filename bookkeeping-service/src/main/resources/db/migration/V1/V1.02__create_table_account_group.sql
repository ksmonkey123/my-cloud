create table account_group
(
    -- id columns
    id           bigint      not null,
    book_id      bigint      not null,
    -- business columns
    title        varchar(50) not null,
    group_number int         not null,
    -- management fields
    cre_user     varchar(30),
    mut_user     varchar(30),
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_account_group primary key (id),
    constraint fk_account_group__book_id foreign key (book_id) references book (id),
    constraint uq_account_group__title_per_book unique (title, book_id),
    constraint uq_account_group__number_per_book unique (group_number, book_id),
    constraint ck_account_group__group_number check ( group_number > 0 and group_number < 10 )
);

create index idx_account_group__book_id on account_group (book_id);
create index idx_account_group__cre_time on account_group (cre_time);
create index idx_account_group__mut_time on account_group (mut_time);

create sequence account_group_seq start 1 increment 50;

create table account_group
(
    -- id columns
    id        bigint      not null,
    book_id   bigint      not null,
    parent_id bigint,
    -- business columns
    title     varchar(50) not null,
    -- management fields
    cre_user  varchar(30),
    mut_user  varchar(30),
    cre_time  timestamp   not null default current_timestamp,
    mut_time  timestamp   not null default current_timestamp,
    version   int         not null default 0,
    -- constraints
    constraint pk_account_group primary key (id),
    constraint fk_account_group__book_id foreign key (book_id) references book (id),
    constraint fk_account_group__parent_id foreign key (parent_id) references account_group (id),
    constraint uq_account_group__title_per_book unique (book_id, title)
);

create index idx_account_group__book_id on account_group (book_id);
create index idx_account_group__cre_time on account_group (cre_time);
create index idx_account_group__mut_time on account_group (mut_time);
create index idx_account_group__parent_id on account_group (parent_id);

create sequence account_group_seq start 1 increment 50;

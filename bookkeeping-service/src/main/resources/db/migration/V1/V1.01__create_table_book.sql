create table book
(
    -- id columns
    id           bigint      not null,
    username     varchar(30) not null,
    -- business columns
    title        varchar(50) not null,
    opening_date date        not null,
    closing_date date        not null,
    -- management fields
    cre_user     varchar(30),
    mut_user     varchar(30),
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_book primary key (id),
    constraint uq_book__title_per_username unique (username, title),
    constraint ck_book__date_logic check ( opening_date < closing_date )
);

create index idx_book__cre_time on book (cre_time);
create index idx_book__mut_time on book (mut_time);

create sequence book_seq start 1 increment 50;

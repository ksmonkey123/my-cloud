create table bookkeeping_book
(
    -- id columns
    id           bigint      not null,
    username     varchar(30) not null,
    -- business columns
    title        varchar(50) not null,
    opening_date date        not null,
    closing_date date        not null,
    description  text,
    closed       boolean     not null,
    -- management fields
    cre_user     varchar(30) not null,
    mut_user     varchar(30) not null,
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_bookkeeping_book primary key (id),
    constraint uq_bookkeeping_book__title_per_username unique (username, title),
    constraint ck_bookkeeping_book__date_logic check ( opening_date < closing_date )
);

create index idx_bookkeeping_book__cre_time on bookkeeping_book (cre_time);
create index idx_bookkeeping_book__mut_time on bookkeeping_book (mut_time);

create table bookkeeping_account_group
(
    -- id columns
    id           bigint      not null,
    book_id      bigint      not null,
    -- business columns
    title        varchar(50) not null,
    group_number int         not null,
    locked       boolean     not null,
    -- management fields
    cre_user     varchar(30) not null,
    mut_user     varchar(30) not null,
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_bookkeeping_account_group primary key (id),
    constraint fk_bookkeeping_account_group__book_id foreign key (book_id) references bookkeeping_book (id),
    constraint uq_bookkeeping_account_group__title_per_book unique (title, book_id),
    constraint uq_bookkeeping_account_group__number_per_book unique (group_number, book_id),
    constraint ck_bookkeeping_account_group__group_number check ( group_number > 0 and group_number < 10 )
);

create index idx_bookkeeping_account_group__book_id on bookkeeping_account_group (book_id);
create index idx_bookkeeping_account_group__cre_time on bookkeeping_account_group (cre_time);
create index idx_bookkeeping_account_group__mut_time on bookkeeping_account_group (mut_time);

create table bookkeeping_account
(
    -- id columns
    id               bigint      not null,
    account_group_id bigint      not null,
    -- business columns
    account_type     varchar(1)  not null,
    account_number   int         not null,
    title            varchar(50) not null,
    description      text,
    locked           boolean     not null,
    -- management fields
    cre_user         varchar(30) not null,
    mut_user         varchar(30) not null,
    cre_time         timestamp   not null default current_timestamp,
    mut_time         timestamp   not null default current_timestamp,
    version          int         not null default 0,
    -- constraints
    constraint pk_bookkeeping_account primary key (id),
    constraint fk_bookkeeping_account__account_group_id foreign key (account_group_id) references bookkeeping_account_group (id),
    constraint uq_bookkeeping_account__title_per_group unique (account_group_id, title),
    constraint uq_bookkeeping_account__number_per_group unique (account_group_id, account_number),
    constraint ck_bookkeeping_account__account_type check (account_type in ('A', 'P', '+', '-')),
    constraint ck_bookkeeping_account__account_number check (account_number > 0 and account_number < 1000)
);

create index idx_bookkeeping_account__account_group_id on bookkeeping_account (account_group_id);
create index idx_bookkeeping_account__cre_time on bookkeeping_account (cre_time);
create index idx_bookkeeping_account__mut_time on bookkeeping_account (mut_time);

create table bookkeeping_booking_record
(
    -- id columns
    id           bigint      not null,
    book_id      bigint      not null,
    -- business columns
    local_id     bigint      not null,
    booking_text varchar(50) not null,
    description  text,
    booking_date date        not null,
    tag          varchar(20),
    -- management fields
    cre_user     varchar(30),
    mut_user     varchar(30),
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_bookkeeping_booking_record primary key (id),
    constraint fk_bookkeeping_booking_record__book_id foreign key (book_id) references bookkeeping_book (id),
    constraint uq_booking_record_local_id_per_book unique (book_id, local_id)
);

create table bookkeeping_booking_movement
(
    -- id columns
    booking_record_id bigint        not null,
    account_id        bigint        not null,
    -- data columns
    amount            numeric(9, 2) not null,
    -- constraints
    constraint pk_bookkeeping_booking_movement primary key (booking_record_id, account_id),
    constraint fk_bookkeeping_booking_movement__booking_record_id foreign key (booking_record_id) references bookkeeping_booking_record (id),
    constraint fk_bookkeeping_booking_movement__account_id foreign key (account_id) references bookkeeping_account (id),
    constraint ck_bookkeeping_booking_movement__amount_not_zero check ( amount <> 0 )
);

create index idx_bookkeeping_booking_record__book_id on bookkeeping_booking_record (book_id);
create index idx_bookkeeping_booking_record__booking_date on bookkeeping_booking_record (booking_date);
create index idx_bookkeeping_booking_record__cre_time on bookkeeping_booking_record (cre_time);
create index idx_bookkeeping_booking_record__mut_time on bookkeeping_booking_record (mut_time);
create index idx_bookkeeping_booking_record__tag_per_book on bookkeeping_booking_record (book_id, tag);

create index idx_bookkeeping_booking_movement__booking_record_id on bookkeeping_booking_movement (booking_record_id);
create index idx_bookkeeping_booking_movement__account_id on bookkeeping_booking_movement (account_id);

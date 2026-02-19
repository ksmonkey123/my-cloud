create table book
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
    version      int         not null default 0,
    -- constraints
    constraint pk_book primary key (id),
    constraint uq_book__title_per_username unique (username, title),
    constraint ck_book__date_logic check ( opening_date < closing_date )
);

create table account_group
(
    -- id columns
    id           bigint      not null,
    book_id      bigint      not null,
    -- business columns
    title        varchar(50) not null,
    group_number int         not null,
    locked       boolean     not null,
    -- management fields
    version      int         not null default 0,
    -- constraints
    constraint pk_account_group primary key (id),
    constraint fk_account_group__book_id foreign key (book_id) references book (id),
    constraint uq_account_group__title_per_book unique (title, book_id),
    constraint uq_account_group__number_per_book unique (group_number, book_id),
    constraint ck_account_group__group_number check ( group_number > 0 and group_number < 10 )
);

create index idx_account_group__book_id on account_group (book_id);

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
    locked           boolean     not null,
    -- management fields
    version          int         not null default 0,
    -- constraints
    constraint pk_account primary key (id),
    constraint fk_account__account_group_id foreign key (account_group_id) references account_group (id),
    constraint uq_account__title_per_group unique (account_group_id, title),
    constraint uq_account__number_per_group unique (account_group_id, account_number),
    constraint ck_account__account_type check (account_type in ('A', 'P', '+', '-')),
    constraint ck_account__account_number check (account_number > 0 and account_number < 1000)
);

create index idx_account__account_group_id on account (account_group_id);

create table booking_record
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
    version      int         not null default 0,
    -- constraints
    constraint pk_booking_record primary key (id),
    constraint fk_booking_record__book_id foreign key (book_id) references book (id),
    constraint uq_booking_record_local_id_per_book unique (book_id, local_id)
);

create table booking_movement
(
    -- id columns
    booking_record_id bigint        not null,
    account_id        bigint        not null,
    -- data columns
    amount            numeric(9, 2) not null,
    -- constraints
    constraint pk_booking_movement primary key (booking_record_id, account_id),
    constraint fk_booking_movement__booking_record_id foreign key (booking_record_id) references booking_record (id),
    constraint fk_booking_movement__account_id foreign key (account_id) references account (id),
    constraint ck_booking_movement__amount_not_zero check ( amount <> 0 )
);

create index idx_booking_record__book_id on booking_record (book_id);
create index idx_booking_record__booking_date on booking_record (booking_date);
create index idx_booking_record__tag_per_book on booking_record (book_id, tag);

create index idx_booking_movement__booking_record_id on booking_movement (booking_record_id);
create index idx_booking_movement__account_id on booking_movement (account_id);

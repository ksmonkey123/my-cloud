create table booking_record
(
    -- id columns
    id           bigint      not null,
    book_id      bigint      not null,
    -- business columns
    booking_text varchar(50) not null,
    description  text,
    invoice_date date        not null,
    booking_date date        not null,
    -- management fields
    cre_user     varchar(30),
    mut_user     varchar(30),
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_booking_record primary key (id),
    constraint fk_booking_record__book_id foreign key (book_id) references book (id),
    constraint ck_booking_record__date_logic check ( invoice_date <= booking_date )
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
    constraint fk_booking_movement__account_id foreign key (account_id) references account (id)
);

create index idx_booking_record__book_id on booking_record (book_id);
create index idx_booking_record__cre_time on booking_record (cre_time);
create index idx_booking_record__mut_time on booking_record (mut_time);

create index idx_booking_movement__booking_record_id on booking_movement (booking_record_id);
create index idx_booking_movement__account_id on booking_movement (account_id);

create sequence booking_record_seq start 1 increment 50;

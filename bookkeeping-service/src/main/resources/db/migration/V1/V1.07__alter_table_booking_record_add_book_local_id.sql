alter table booking_record
    add column local_id bigint;

update booking_record
set local_id = id;

alter table booking_record
    alter column local_id set not null;

alter table booking_record
    add constraint uq_booking_record_local_id_per_book unique (book_id, local_id);
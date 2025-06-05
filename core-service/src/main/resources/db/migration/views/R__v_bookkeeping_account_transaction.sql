drop view if exists v_bookkeeping_account_transaction;

create view v_bookkeeping_account_transaction
            (record_id,
             account_id,
             booking_date,
             booking_text,
             amount,
             tag,
             description,
             cre_time)
as
(
select r.local_id,
       m.account_id,
       r.booking_date,
       r.booking_text,
       m.amount,
       r.tag,
       r.description,
       r.cre_time
from bookkeeping_booking_record r,
     bookkeeping_booking_movement m
where r.id = m.booking_record_id);
drop view if exists v_account_transaction;

create view v_account_transaction
            (record_id,
             account_id,
             booking_date,
             booking_text,
             amount,
             tag,
             description)
as
(
select r.id,
       m.account_id,
       r.booking_date,
       r.booking_text,
       m.amount,
       r.tag,
       r.description
from booking_record r,
     booking_movement m
where r.id = m.booking_record_id
order by r.booking_date asc,
         r.cre_time asc);
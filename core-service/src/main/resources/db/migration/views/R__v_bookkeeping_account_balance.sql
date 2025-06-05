drop view if exists v_bookkeeping_account_balance;

create view v_bookkeeping_account_balance
            (account_id,
             balance)
as
(
select bm.account_id,
       sum(bm.amount) as balance
from bookkeeping_booking_record br,
     bookkeeping_booking_movement bm
where br.id = bm.booking_record_id
group by bm.account_id);
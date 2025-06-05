drop view if exists v_bookkeeping_account_tag_balance;

create view v_bookkeeping_account_tag_balance(account_id, tag, balance)
as
(
select bm.account_id,
       coalesce(br.tag, ''),
       sum(bm.amount)
from bookkeeping_booking_movement bm
         inner join bookkeeping_booking_record br on bm.booking_record_id = br.id
group by bm.account_id, br.tag
    );
drop view if exists v_account_tag_balance;

create view v_account_tag_balance(account_id, tag, balance)
as
(
select bm.account_id,
       coalesce(br.tag, ''),
       sum(bm.amount)
from booking_movement bm
         inner join booking_record br on bm.booking_record_id = br.id
group by bm.account_id, br.tag
    );
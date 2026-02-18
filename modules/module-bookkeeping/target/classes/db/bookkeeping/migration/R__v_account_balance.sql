drop view if exists v_account_balance;

create view v_account_balance
            (account_id,
             balance)
as
(
select bm.account_id,
       sum(bm.amount) as balance
from booking_record br,
     booking_movement bm
where br.id = bm.booking_record_id
group by bm.account_id);
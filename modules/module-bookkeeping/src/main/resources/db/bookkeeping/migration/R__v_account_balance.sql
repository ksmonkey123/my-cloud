drop view if exists v_account_balance;

create view v_account_balance as
select account_id  as account_id,
       sum(amount) as balance
from booking_movement
group by account_id;
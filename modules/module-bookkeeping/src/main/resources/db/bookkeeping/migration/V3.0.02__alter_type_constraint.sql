alter table account drop constraint ck_account__account_type;
alter table account add constraint ck_account__account_type check (account_type in ('A', 'P', 'X', '+', '-'));

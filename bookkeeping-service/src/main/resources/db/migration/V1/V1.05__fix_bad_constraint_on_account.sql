alter table account
    drop constraint uq_account__number_per_group;

alter table account
    add constraint uq_account__number_per_group unique (account_group_id, account_number);
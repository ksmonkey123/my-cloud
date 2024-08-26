alter table account
    add column locked boolean not null default false;

alter table account_group
    add column locked boolean not null default false;
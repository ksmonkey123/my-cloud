drop table api_key_role;

alter table api_key
    add column authorities text;

alter table role
    add column authorities text;
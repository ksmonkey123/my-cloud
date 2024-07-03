alter table account
    add column cre_user varchar(30),
    add column mut_user varchar(30),
    add column cre_time timestamp not null default current_timestamp,
    add column mut_time timestamp not null default current_timestamp,
    add column version  int       not null default 0;

alter table role
    add column cre_user varchar(30),
    add column mut_user varchar(30),
    add column cre_time timestamp not null default current_timestamp,
    add column mut_time timestamp not null default current_timestamp,
    add column version  int       not null default 0;

alter table auth_token
    add column cre_user varchar(30),
    add column mut_user varchar(30),
    add column cre_time timestamp not null default current_timestamp,
    add column mut_time timestamp not null default current_timestamp,
    add column version  int       not null default 0;

update auth_token
set cre_time = creation_time,
    mut_time = creation_time;

alter table auth_token
    drop column creation_time;

create index idx_account__cre_time on account (cre_time);
create index idx_account__mut_time on account (mut_time);
create index idx_role__cre_time on role (cre_time);
create index idx_role__mut_time on role (mut_time);
create index idx_auth_token__cre_time on auth_token (cre_time);
create index idx_auth_token__mut_time on auth_token (mut_time);

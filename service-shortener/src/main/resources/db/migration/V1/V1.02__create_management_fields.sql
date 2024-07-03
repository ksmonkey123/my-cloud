alter table short_link
    add column cre_user varchar(30),
    add column mut_user varchar(30),
    add column cre_time timestamp not null default current_timestamp,
    add column mut_time timestamp not null default current_timestamp,
    add column version  int       not null default 0;

update short_link
set cre_time = creation_time,
    mut_time = creation_time;

alter table short_link
    drop column creation_time;

create index idx_short_link__cre_time on short_link (cre_time);
create index idx_short_link__mut_time on short_link (mut_time);

alter table log_entry
    add column cre_user varchar(30),
    add column mut_user varchar(30),
    add column cre_time timestamp not null default current_timestamp,
    add column mut_time timestamp not null default current_timestamp,
    add column version  int       not null default 0;

update log_entry
set cre_time = timestamp_end,
    mut_time = timestamp_end;

create index idx_log_entry__cre_time on log_entry (cre_time);
create index idx_log_entry__mut_time on log_entry (mut_time);

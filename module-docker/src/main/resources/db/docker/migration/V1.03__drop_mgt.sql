alter table docker_entry_state drop column cre_user;
alter table docker_entry_state drop column mut_user;
alter table docker_entry_state rename column cre_time to recordet_at;
alter table docker_entry_state drop column mut_time;

alter table docker_monitored_entry drop column cre_user;
alter table docker_monitored_entry drop column mut_user;
alter table docker_monitored_entry drop column cre_time;
alter table docker_monitored_entry drop column mut_time;

alter table docker_monitored_entry add owner varchar(30);
alter table web_monitored_site add owner varchar(30);

create index idx_docker_monitored_entry__owner on docker_monitored_entry (owner);
create index idx_web_monitored_site__owner on web_monitored_site (owner);

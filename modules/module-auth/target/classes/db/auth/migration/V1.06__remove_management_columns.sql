alter table account drop column cre_user;
alter table account drop column mut_user;
alter table account drop column cre_time;
alter table account drop column mut_time;

alter table role drop column cre_user;
alter table role drop column mut_user;
alter table role drop column cre_time;
alter table role drop column mut_time;

alter table auth_token drop column cre_user;
alter table auth_token drop column mut_user;
alter table auth_token drop column cre_time;
alter table auth_token drop column mut_time;

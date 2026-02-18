alter table book drop column cre_user;
alter table book drop column mut_user;
alter table book drop column cre_time;
alter table book drop column mut_time;

alter table account_group drop column cre_user;
alter table account_group drop column mut_user;
alter table account_group drop column cre_time;
alter table account_group drop column mut_time;

alter table account drop column cre_user;
alter table account drop column mut_user;
alter table account drop column cre_time;
alter table account drop column mut_time;

alter table booking_record drop column cre_user;
alter table booking_record drop column mut_user;
alter table booking_record drop column cre_time cascade;
alter table booking_record drop column mut_time;

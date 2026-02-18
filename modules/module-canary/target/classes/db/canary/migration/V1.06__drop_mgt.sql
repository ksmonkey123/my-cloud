alter table web_monitored_site drop column cre_user;
alter table web_monitored_site drop column mut_user;
alter table web_monitored_site drop column cre_time;
alter table web_monitored_site drop column mut_time;

alter table web_test_record drop column cre_user;
alter table web_test_record drop column mut_user;
alter table web_test_record rename column cre_time to recorded_at;
alter table web_test_record drop column mut_time;


alter table job rename column cre_time to created_at;
alter table job drop column mut_time;
alter table job drop column cre_user;
alter table job drop column mut_user;

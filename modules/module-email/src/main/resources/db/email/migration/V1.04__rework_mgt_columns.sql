alter table outbox rename column cre_time to created_at;
alter table outbox rename column mut_time to sent_at;
alter table outbox alter column sent_at drop default;
alter table outbox drop column cre_user;
alter table outbox drop column mut_user;

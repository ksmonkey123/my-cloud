alter table outbox alter column body_format type varchar(8);
alter table outbox drop constraint ck_outbox__body_format;
alter table outbox add constraint ck_outbox__body_format check (body_format in ('HTML', 'TEXT', 'MARKDOWN'));
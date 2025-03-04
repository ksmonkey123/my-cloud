alter table account add column language varchar(2);

update account set language = 'en' where language is null;

alter table account alter column language set not null;
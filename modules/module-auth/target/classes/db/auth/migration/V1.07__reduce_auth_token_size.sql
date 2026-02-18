truncate table auth_token;

alter table auth_token alter column token_string type varchar(43);

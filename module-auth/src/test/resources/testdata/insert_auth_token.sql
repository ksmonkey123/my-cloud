delete from auth.auth_token;

insert into auth.auth_token (id, token_string, account_id, valid_until)
values (nextval('public.hibernate_seq'),
        'valid_token',
        (select id from auth.account where username = 'admin'),
        current_timestamp + interval '1 hour');

insert into auth.auth_token(id, token_string, account_id, valid_until)
values (nextval('public.hibernate_seq'),
        'expired_token',
        (select id from auth.account where username = 'admin'),
        current_timestamp - interval '1 hour');

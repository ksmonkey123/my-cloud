insert into auth.account (id, username, password, enabled, admin, language, email)
values (nextval('public.hibernate_seq'),
        'user',
        'password',
        true,
        false,
        'en',
        null);

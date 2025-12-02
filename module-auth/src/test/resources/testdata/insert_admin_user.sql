insert into auth.account (id, username, password, enabled, admin, language, email)
values (nextval('public.hibernate_seq'),
        'admin2',
        'password',
        false,
        true,
        'en',
        null);

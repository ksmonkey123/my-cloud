insert into auth.account (id, username, password, enabled, admin, language, email)
values (1000,
        'test-user-1000',
        '$2a$10$Vi4FiOp4DRqw7VF0/RPAweP6dM0HVhp2buC6CCAXOphQXDbpBZiIO', -- 'password'
        true,
        false,
        'en',
        null);

insert into auth.auth_token (id, token_string, account_id, valid_until)
values (1001, 'MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE', 1000, current_timestamp + interval '1 hour');

insert into auth.auth_token(id, token_string, account_id, valid_until)
values (1002, 'OTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE', 1000, current_timestamp - interval '1 hour');

insert into auth.account (id, username, password, enabled, admin, language, email)
values (1100,
        'test-user-1100',
        '$2a$10$Vi4FiOp4DRqw7VF0/RPAweP6dM0HVhp2buC6CCAXOphQXDbpBZiIO', -- 'password'
        false,
        false,
        'en',
        null);

insert into auth.auth_token (id, token_string, account_id, valid_until)
values (1101, 'QDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE', 1100, current_timestamp + interval '1 hour');

insert into auth.account (id, username, password, enabled, admin, language, email)
values (3000,
        'test-user-3000',
        '$2a$10$Vi4FiOp4DRqw7VF0/RPAweP6dM0HVhp2buC6CCAXOphQXDbpBZiIO', -- 'password'
        true,
        false,
        'en',
        null);

insert into auth.auth_token(id, token_string, account_id, valid_until)
values (3001,
        'yH/Q7WKAW1xb6yVnW1rovIG6nbj+GxEbcWWrKJavLIU',
        3000,
        current_timestamp + interval '1 hour');
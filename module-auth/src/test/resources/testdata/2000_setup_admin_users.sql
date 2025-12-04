insert into auth.account (id, username, password, enabled, admin, language, email)
values (2000,
        'test-user-2000',
        '$2a$10$Vi4FiOp4DRqw7VF0/RPAweP6dM0HVhp2buC6CCAXOphQXDbpBZiIO', -- 'password'
        true,
        true,
        'en',
        null);

insert into auth.account (id, username, password, enabled, admin, language, email)
values (2001,
        'test-user-2001',
        '$2a$10$Vi4FiOp4DRqw7VF0/RPAweP6dM0HVhp2buC6CCAXOphQXDbpBZiIO', -- 'password'
        false,
        true,
        'en',
        null);

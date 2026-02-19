create table link
(
    -- id columns
    id         varchar(8)  not null,
    username   varchar(30) not null,
    -- business columns
    target_url text        not null,
    -- management columns
    version    int         not null default 0,
    -- constraints
    constraint pk_link primary key (id)
);

create index idx_link__username on link (username);


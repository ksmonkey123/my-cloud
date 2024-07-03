create table short_link
(
    -- id columns
    id            varchar(8)  not null,
    username      varchar(30) not null,
    -- business columns
    target_url    text        not null,
    creation_time timestamp   not null,
    -- constraints
    constraint pk_short_link primary key (id)
);

create index idx_short_link__username on short_link (username);
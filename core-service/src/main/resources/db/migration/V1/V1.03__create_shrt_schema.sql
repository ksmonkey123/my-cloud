create table shrt_short_link
(
    -- id columns
    id         varchar(8)  not null,
    username   varchar(30) not null,
    -- business columns
    target_url text        not null,
    -- management columns
    cre_user   varchar(30) not null,
    mut_user   varchar(30) not null,
    cre_time   timestamp   not null default current_timestamp,
    mut_time   timestamp   not null default current_timestamp,
    version    int         not null default 0,
    -- constraints
    constraint pk_short_link primary key (id)
);

create index idx_short_link__username on shrt_short_link (username);
create index idx_short_link__cre_time on shrt_short_link (cre_time);
create index idx_short_link__mut_time on shrt_short_link (mut_time);

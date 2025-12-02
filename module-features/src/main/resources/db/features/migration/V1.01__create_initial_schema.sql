create table feature_flag
(
    -- id columns
    id          varchar(30) not null,
    -- business columns
    enabled     boolean     not null,
    -- management columns
    version     int         not null default 0,
    --constraints
    constraint pk_feature_flag primary key (id)
)

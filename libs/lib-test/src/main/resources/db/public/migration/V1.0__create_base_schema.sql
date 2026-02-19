create table shedlock
(
    name       varchar(64)  not null,
    lock_until timestamp    not null,
    locked_at  timestamp    not null,
    locked_by  varchar(255) not null,
    constraint pk_shedlock primary key ("name")
);

create sequence HIBERNATE_SEQ start 1000000 increment 1;
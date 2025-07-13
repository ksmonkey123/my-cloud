create table job
(
    -- id columns
    id           bigint      not null,
    uuid         uuid        not null,
    -- business columns
    owner        varchar(30) not null,
    url          text        not null,
    format       varchar(5)  not null,
    status       varchar(9)  not null,
    submitted_at timestamp,
    started_at   timestamp,
    completed_at timestamp,
    -- management columns
    cre_user     varchar(30) not null,
    mut_user     varchar(30) not null,
    cre_time     timestamp   not null default current_timestamp,
    mut_time     timestamp   not null default current_timestamp,
    version      int         not null default 0,
    -- constraints
    constraint pk_job primary key (id),
    constraint uq_job__uuid unique (uuid),
    constraint ck_job__status check (status in ('PENDING', 'SUBMITTED', 'RUNNING', 'COMPLETED')),
    constraint ck_job__format check (format in ('FULL', 'VIDEO', 'AUDIO', 'RAW'))
);

create index idx_job_owner on job (owner, cre_time);
create index idx_job_pending on job (status, cre_time) where status <> 'COMPLETED';
create table job_files
(
    job_id    bigint        not null,
    file_name varchar(4096) not null,
    file_size bigint        not null,
    constraint pk_job_files primary key (job_id, file_name)
);
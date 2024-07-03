create table log_entry
(
    id              bigint    not null,
    trace_root      text      not null,
    trace_id        text      not null,
    service_name    text      not null,
    account         varchar(30),
    -- rest info
    verb            varchar(7),
    path            text,
    -- method info
    component       text      not null,
    method          text      not null,
    error           text,
    -- timing info
    timestamp_start timestamp not null,
    timestamp_end   timestamp not null,
    -- constraints
    constraint pk_log_entry primary key (id),
    constraint ck_log_entry__rest check ((verb is null) = (path is null)),
    constraint uq_log_entry__trace_id unique (trace_id)
);

create sequence log_entry_seq start 1 increment 50;

create index idx_log_entry__trace_id on log_entry (trace_id);
create index idx_log_entry__trace_root on log_entry (trace_root);
create index idx_log_entry__timestamp_start on log_entry (timestamp_start);

create table method_parameter
(
    log_entry_id bigint not null,
    name         text   not null,
    value        text,

    -- constraints
    constraint pk_method_paramter primary key (log_entry_id, name),
    constraint fk_method_parameter__log_entry_id foreign key (log_entry_id) references log_entry (id)
);

create index idx_method_parameter__log_entry_id on method_parameter (log_entry_id);
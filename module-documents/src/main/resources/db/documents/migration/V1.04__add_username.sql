alter table document add username varchar(30);
alter table document add created_at timestamp not null default current_timestamp;

create index idx_document__username on document (username, created_at);
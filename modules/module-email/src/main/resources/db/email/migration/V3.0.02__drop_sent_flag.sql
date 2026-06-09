alter table outbox
    drop column sent;

create index idx_outbox_unsent_cre_time on outbox (created_at) where sent_at is null;

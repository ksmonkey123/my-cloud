alter table outbox
    add message_uid  text,
    add constraint uq_outbox__message_uid unique (message_uid);

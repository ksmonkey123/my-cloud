alter table document
    add source text;

update document
set source = 'BOOKKEEPING';

alter table document
    alter column source set not null;

alter table document
    add constraint ck_document_source check (source in ('BOOKKEEPING'));

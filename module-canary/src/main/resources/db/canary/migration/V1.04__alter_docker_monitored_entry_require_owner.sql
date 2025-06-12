delete
from docker_entry_state
where monitored_entry_id in (select id
                             from docker_monitored_entry
                             where owner is null);

delete
from docker_monitored_entry
where owner is null;

alter table docker_monitored_entry
    alter column owner set not null;

delete
from web_site_test
where site_id in (select id from web_monitored_site where owner is null);

delete
from web_failed_test
where record_id in (select id
                    from web_test_record
                    where site_id in (select id
                                      from web_monitored_site
                                      where owner is null));

delete
from web_test_record
where site_id in (select id
                  from web_monitored_site
                  where owner is null);

delete
from web_monitored_site
where owner is null;

alter table web_monitored_site
    alter column owner set not null;

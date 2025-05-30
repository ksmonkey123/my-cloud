drop view if exists v_docker_current_state;

create view v_docker_current_state
            (
             monitored_entry_id,
             digest,
             tags,
             recorded_at)
as
select des.monitored_entry_id,
       des.digest,
       des.tags,
       des.cre_time
from docker_entry_state des
where des.id = (select max(id) from docker_entry_state des2 where des2.monitored_entry_id = des.monitored_entry_id);
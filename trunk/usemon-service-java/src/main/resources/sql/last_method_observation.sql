-- Retrieves the timestamp of the latest method observation
select
  timestamp(d.date_v, t.time_v)
from
  method_measurement_fact m
  join d_date d on d.id = m.d_date_id
  join d_time t on t.id = m.d_time_id
where
  m.id = (select max(id) from method_measurement_fact)

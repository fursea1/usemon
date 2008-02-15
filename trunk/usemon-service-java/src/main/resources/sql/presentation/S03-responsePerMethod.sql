/* Hvilke metoder har høyest responstid */
select
  class,
  method,
  sum(invocation_count) as "invocation count",
  avg(max_response_time) as "max response time",
  avg(avg_response_time) as "avg response time",
  sum(checked_exceptions) as "checked exceptions",
  sum(unchecked_exceptions) as "unchecked exceptions"
from
  method_measurement_fact as m
  join class on class.id = m.class_id
  join method on method.id = m.method_id

where
  1=1
  -- and method like 'get%'
group by
  1,2
order by
  5 desc
limit 30

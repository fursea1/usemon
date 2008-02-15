/* Highest response times per principal, class and method */
select
  principal,
  class,
  method,
  sum(invocation_count),
  avg(max_response_time) ,
  avg(avg_response_time),
  sum(checked_exceptions),
  sum(unchecked_exceptions)
from
  method_measurement_fact as m
  join class on class.id = m.class_id
  join method on method.id = m.method_id
  join principal on principal.id = m.principal_id
where
  1=1
  -- and method like 'get%'
group by
  1,2,3
order by
  6 desc
limit 30

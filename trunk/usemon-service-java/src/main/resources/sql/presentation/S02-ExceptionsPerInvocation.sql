/* Exceptions per invocation */
select
  class,
  method,
  sum(invocation_count),
  sum(checked_exceptions),
  sum(checked_exceptions) /  sum(invocation_count)
from
  method_measurement_fact as m
  join class on class.id=m.class_id
  join method on method.id=m.method_id
group by
  1,2
having sum(invocation_count) > 100
order by 5 desc
limit 30

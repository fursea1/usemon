select
  target_class,
  src_class,
  sum(invocation_count) as cnt
from
  invocation_fact as ifa
  join target_class on target_class.id=ifa.target_class_id
  join src_class on src_class.id=ifa.src_class_id
group by 1,2
order by cnt desc

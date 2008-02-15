select
  timestamp(date_v, time_v) as datetime_v,
  sum(invocation_count),
  avg(max_response_time),
  avg(avg_response_time),
  sum(checked_exceptions),
  sum(unchecked_exceptions)
from
  d_time as t
  join method_measurement_fact as m on t.id=m.d_time_id
  join d_date as d on d.id=m.d_date_id
where
  1=1
  and date_v <= (select  date_v 
  				from  
  					method_measurement_fact as m
  					join d_date as d on d.id=m.d_date_id
				where
  					1=1
  					and m.id = (select max(id) from method_measurement_fact)
				)
group by
  datetime_v
order by
  datetime_v
desc limit 30

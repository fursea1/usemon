select
  src_instance,
  src_package,
  src_class,
  target_instance,
  target_package,
  target_class,
  count(*) as "invocation_count"
from
  invocation_fact as i
  join src_instance on i.src_instance_id=src_instance.id
  join target_instance on i.target_instance_id=target_instance.id
  join src_package on i.src_package_id=src_package.id
  join target_package on i.target_package_id=target_package.id
  join src_class on i.src_class_id=src_class.id
  join target_class on i.target_class_id=target_class.id
  join d_date on d_date.id=i.d_date_id
  join location on location.id=i.location_id
where
  1=1
  and d_date.date_v between date_sub(curdate(), interval 3 day) and curdate()
group by
  1,2,3,4,5,6
order by
  7 desc
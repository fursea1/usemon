/* Which method is invoked from a given method */
select
  target_class.target_class,
  target_method.target_method,
  sum(invocation_count)
from
  invocation_fact f
  join target_class on target_class.id=f.target_class_id
  join src_class on src_class.id=f.src_class_id
  join target_method on target_method.id=f.target_method_id
  join src_method on src_method.id=f.src_method_id
where
  src_class.src_class = 'InvoiceServiceBean'
  and src_method.src_method= 'checkInstallmentApprovalAcceptReminder'
group by 1,2

limit 30

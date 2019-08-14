-- non agg, non corr
explain
select * 
from src 
where src.key not in  
  ( select key  from src s1 
    where s1.key > '2'
  )
;

select * 
from src 
where src.key not in  ( select key from src s1 where s1.key > '2')
order by key
;

-- non agg, corr
explain
select p_mfgr, b.p_name, p_size 
from part b 
where b.p_name not in 
  (select p_name 
  from (select p_mfgr, p_name, p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2 and b.p_mfgr = a.p_mfgr 
  )
;

select p_mfgr, b.p_name, p_size 
from part b 
where b.p_name not in 
  (select p_name 
  from (select p_mfgr, p_name, p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2 and b.p_mfgr = a.p_mfgr 
  )
order by p_mfgr, b.p_name
;

-- agg, non corr
explain
select p_name, p_size 
from 
part where part.p_size not in 
  (select avg(p_size) 
  from (select p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2
  )
;
select p_name, p_size 
from 
part where part.p_size not in 
  (select avg(p_size) 
  from (select p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2
  )
order by p_name, p_size
;

-- agg, corr
explain
select p_mfgr, p_name, p_size 
from part b where b.p_size not in 
  (select min(p_size) 
  from (select p_mfgr, p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2 and b.p_mfgr = a.p_mfgr
  )
;

select p_mfgr, p_name, p_size 
from part b where b.p_size not in 
  (select min(p_size) 
  from (select p_mfgr, p_size, rank() over(partition by p_mfgr order by p_size) as r from part) a 
  where r <= 2 and b.p_mfgr = a.p_mfgr
  )
order by p_mfgr, p_size
;

-- non agg, non corr, Group By in Parent Query
select li.l_partkey, count(*) 
from lineitem li 
where li.l_linenumber = 1 and 
  li.l_orderkey not in (select l_orderkey from lineitem where l_shipmode = 'AIR') 
group by li.l_partkey
;

-- alternate not in syntax
select * 
from src 
where not src.key in  ( select key from src s1 where s1.key > '2')
order by key
;

-- null check
create view T1_v as 
select key from src where key <'11';

create view T2_v as 
select case when key > '104' then null else key end as key from T1_v;

explain
select * 
from T1_v where T1_v.key not in (select T2_v.key from T2_v);

select * 
from T1_v where T1_v.key not in (select T2_v.key from T2_v);

create table L as select 4436 id;
create table LA as select 4436 loan_id, 4748 aid, 4415 pi_id;
create table FR as select 4436 loan_id;
create table A as select 4748 id;
create table PI as select 4415 id;

create table acct as select 4748 aid, 10 acc_n, 122 brn;
insert into table acct values(4748, null, null);
insert into table acct values(4748, null, null);

--[HIVE-10841] (WHERE col is not null) does not work sometimes for queries with many JOIN statements
explain select
  acct.ACC_N,
  acct.brn
FROM L
JOIN LA ON L.id = LA.loan_id
JOIN FR ON L.id = FR.loan_id
JOIN A ON LA.aid = A.id
JOIN PI ON PI.id = LA.pi_id
JOIN acct ON A.id = acct.aid
WHERE
  L.id = 4436
  and acct.brn is not null;

select
  acct.ACC_N,
  acct.brn
FROM L
JOIN LA ON L.id = LA.loan_id
JOIN FR ON L.id = FR.loan_id
JOIN A ON LA.aid = A.id
JOIN PI ON PI.id = LA.pi_id
JOIN acct ON A.id = acct.aid
WHERE
  L.id = 4436
  and acct.brn is not null;

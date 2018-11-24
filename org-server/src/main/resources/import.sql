-- 同步现有spis机构数据
insert into org (id,name,flag,province,city) select id,name,flag,province,city from spischolar.t_org;

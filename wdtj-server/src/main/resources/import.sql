-- 初始化测试数据
INSERT INTO tj_date_setting (gmt_create, gmt_modified, date_index, date_type, weight)
values (now(), now(), 1, 1, 0.3),
       (now(), now(), 2, 1, 0.7),
       (now(), now(), 3, 1, 1.3),
       (now(), now(), 7, 1, 1.8),
       (now(), now(), 8, 1, 2.9),
       (now(), now(), 1, 4, 0.2),
       (now(), now(), 2, 4, 0.3),
       (now(), now(), 3, 4, 0.5),
       (now(), now(), 4, 4, 0.6),
       (now(), now(), 8, 4, 1.6),
       (now(), now(), 9, 4, 3.6);

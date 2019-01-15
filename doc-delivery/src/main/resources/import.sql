-- 创建触发器,注意语句不要格式化，不要换行！
# CREATE TRIGGER insert_literature_gmt_create BEFORE INSERT ON literature FOR EACH ROW BEGIN SET new.gmt_create = now(); END;
# CREATE TRIGGER update_literature_gmt_modified BEFORE UPDATE ON literature FOR EACH ROW BEGIN SET new.gmt_modified = now(); END;
#
# CREATE TRIGGER insert_help_record_gmt_create BEFORE INSERT ON help_record FOR EACH ROW BEGIN SET new.gmt_create = now(); END;
# CREATE TRIGGER update_help_record_gmt_modified BEFORE UPDATE ON help_record FOR EACH ROW BEGIN SET new.gmt_modified = now(); END;
#
# CREATE TRIGGER insert_audit_msg_gmt_create BEFORE INSERT ON audit_msg FOR EACH ROW BEGIN SET new.gmt_create = now(); END;
# CREATE TRIGGER update_audit_msg_gmt_modified BEFORE UPDATE ON audit_msg FOR EACH ROW BEGIN SET new.gmt_modified = now(); END;
#
# CREATE TRIGGER insert_give_record_gmt_create BEFORE INSERT ON give_record FOR EACH ROW BEGIN SET new.gmt_create = now(); END;
# CREATE TRIGGER update_give_record_gmt_modified BEFORE UPDATE ON give_record FOR EACH ROW BEGIN SET new.gmt_modified = now(); END;
#
# CREATE TRIGGER insert_doc_file_gmt_create BEFORE INSERT ON doc_file FOR EACH ROW BEGIN SET new.gmt_create = now(); END;
# CREATE TRIGGER update_doc_file_gmt_modified BEFORE UPDATE ON doc_file FOR EACH ROW BEGIN SET new.gmt_modified = now(); END;

-- 初始化测试数据
# INSERT INTO audit_msg (msg)
# VALUES ("文不对题"), ("文档无法打开"), ("文档错误");
-- insert into literature (doc_title,doc_href) select title,url FROM spischolar.t_delivery GROUP BY title,url,path;
-- INSERT INTO help_record ( literature_id, helper_email, help_channel, helper_scname, helper_id ) SELECT t2.id, t1.email, t1.product_id, t1.org_name, t1.member_id FROM spischolar.t_delivery t1, literature t2 WHERE t1.title = t2.doc_title AND t1.url = t2.doc_href;
-- INSERT INTO give_record ( help_record_id, auditor_id, auditor_name, giver_type ) SELECT t3.id, t1.procesor_id, t1.procesor_name, t1.process_type FROM spischolar.t_delivery t1, literature t2, help_record t3 WHERE t1.title = t2.doc_title AND t1.url = t2.doc_href AND t2.id = t3.literature_id;


-- drop PROCEDURE IF EXISTS give_timeout;
-- DROP EVENT IF EXISTS e_give_timeout;
-- CREATE PROCEDURE give_timeout () BEGIN DECLARE helpRecordId INT DEFAULT 0 ; SELECT help_record_id INTO helpRecordId FROM give_record WHERE doc_file_id IS NULL AND 15 < TIMESTAMPDIFF(MINUTE, gmt_create, now()) ; DELETE FROM give_record WHERE help_record_id = helpRecordId AND doc_file_id IS NULL ; UPDATE help_record SET STATUS = 0 WHERE STATUS = 1 AND id = helpRecordId ; END;
-- CREATE EVENT e_give_timeout ON SCHEDULE EVERY 60 SECOND STARTS TIMESTAMP '2018-05-31 00:00:00' ON COMPLETION PRESERVE DO CALL give_timeout ();


-- ALTER TABLE `help_record`
--  ADD COLUMN `is_anonymous`  bit(1) NULL DEFAULT b'0' COMMENT '0：不匿名，1：匿名' AFTER `literature_id`,
--  ADD COLUMN `is_send`  bit(1) NULL DEFAULT b'1' COMMENT '0：未发送邮件，1：已发送邮件' AFTER `is_anonymous`,
--  ADD COLUMN `remark`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '求助详情' AFTER `is_send`;



# insert into channel (id,name,url,template) values (1,"QQ",null,null),(2,"Spischolar学术资源在线","http://www.spischolar.com","spis/%s.ftl"),(3,"智汇云","http://www.yunscholar.com","zhy/%s.ftl"),(4,"crscholar核心论文库","http://www.crscholar.com","crs/%s.ftl");
#
drop view v_help_record;
CREATE VIEW v_help_record AS SELECT
                                    t1.id AS id,
                                    t1.gmt_create,
                                    t1.gmt_modified,
                                    t1.helper_email,
                                    t1.helper_id,
                                    t1.helper_ip,
                                    t1.helper_name,
                                    t1.helper_scid,
                                    t1.helper_scname,
                                    t1.help_channel,
                                    t3.name as channel_name,
                                    t3.url as channel_url,
                                    t3.template as channel_template,
                                    t3.bccs as bccs,
                                    t3.exp as exp,
                                    t1.is_anonymous,
                                    t1.is_send,
                                    t1.literature_id,
                                    t1.`status`,
                                    t1.remark,
                                    t2.doc_title,
                                    t2.doc_href,
                                    t2.`authors`,
                                    t2.doi,
                                    t2.summary,
                                    t2.unid,
                                    t2.year_of_publication
                             FROM
                                  help_record t1,
                                  literature t2,
                                  channel t3
                             WHERE
                                 t1.literature_id = t2.id and t1.help_channel = t3.id;

drop view v_literature;
create view v_literature as SELECT
                                   `t1`.`id` AS `id`,
                                   `t1`.`gmt_create` AS `gmt_create`,
                                   `t1`.`gmt_modified` AS `gmt_modified`,
                                   `t1`.`doc_href` AS `doc_href`,
                                   `t1`.`doc_title` AS `doc_title`,
                                   `t1`.`unid` AS `unid`,
                                   `t1`.`authors` AS `authors`,
                                   `t1`.`year_of_publication` AS `year_of_publication`,
                                   `t1`.`doi` AS `doi`,
                                   `t1`.`summary` AS `summary`,
                                   `t2`.`is_reusing` AS `is_reusing`,
                                   `t2`.`file_id` AS `file_id`
                            FROM `literature` `t1` JOIN `doc_file` `t2`
                            WHERE `t1`.`id` = `t2`.`literature_id`;
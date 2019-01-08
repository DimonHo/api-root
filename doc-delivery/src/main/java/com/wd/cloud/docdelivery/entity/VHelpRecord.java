package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/1/5
 * @Description: v_help_record视图类
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "v_help_record")
public class VHelpRecord {


    @Id
    private Long helpRecordId;

    private Date gmtCreate;

    private Date gmtModified;

    private Long literatureId;

    private String docTitle;

    private String docHref;

    private String authors;

    private String yearOfPublication;

    private String doi;

    private String summary;

    private String helperEmail;

    private Long helperId;

    private String helperName;

    private Long helperScid;

    private String helperScname;

    private String helperIp;

    private Long helpChannel;

    private String channelName;

    private String channelUrl;

    private String channelTemplate;

    private String bccs;

    private Long exp;

    private int status;

    private boolean send;

    @Column(name = "is_anonynous")
    private boolean anonymous;

    private String unid;

    private String remark;
}

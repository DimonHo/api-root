package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
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

    private int helpChannel;

    private int status;

    @Column(name = "is_send")
    private boolean send;

    @Column(name = "is_anonymous")
    private boolean anonymous;

    private String unid;

    private String remark;
}

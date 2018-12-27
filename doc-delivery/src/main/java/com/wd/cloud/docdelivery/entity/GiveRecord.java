package com.wd.cloud.docdelivery.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description: 应助记录
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "give_record")
public class GiveRecord extends AbstractEntity {

    /**
     * 一个求助可能有多个应助，但只有一个应助有效，失败的应助作为应助记录存在
     */
    private Long helpRecordId;

    /**
     * 文件ID
     */
    private Long docFileId;

    /**
     * 应助者ID
     */
    private Long giverId;

    /**
     * 应助者名称
     */
    private String giverName;

    /**
     * 应助者IP
     */
    private String giverIp;

    /**
     * 应助者类型：
     * 0：系统自动应助，
     * 1：管理员应助
     * 2：用户应助
     */
    @Column(columnDefinition = "tinyint COMMENT '应助者类型： 0：系统自动应助， 1：管理员应助 2：用户应助'")
    private Integer giverType;

    /**
     * 0，待上传，1，求助第三方，2，应助成功，3，应助失败
     */
    private Integer giveStatus;

    /**
     * 0：待审核，1：审核通过，2：审核不通过，4：待上传
     */
    @Column(columnDefinition = "tinyint COMMENT '0：待审核，1：审核通过，2：审核不通过'")
    private Integer auditStatus;


    /**
     * 审核人
     */
    private Long auditorId;

    /**
     * 审核人名称
     */
    private String auditorName;

    /**
     * 审核失败原因
     */
    private Long auditMsgId;
}

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
    private String fileId;

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
     * 0：系统自动复用应助，
     * 1：管理员应助
     * 2：用户应助
     * 3：平台数据库应助
     */
    @Column(columnDefinition = "tinyint COMMENT '应助类型： 0：系统自动应助，1：管理员应助，2：用户应助，3：平台数据库应助'")
    private Integer type;

    /**
     * 0：待上传，1：已取消，2：待审核，3：求助第三方，4：已超时，5：审核已通过，6：审核未通过，7：直接处理成功，8：无结果
     */
    @Column(columnDefinition = "tinyint COMMENT '应助者类型： 0：待上传，1：待审核，2：求助第三方，3：已取消，4：已超时，5：审核已通过，6：审核未通过，7：直接处理成功，8：无结果'")
    private Integer status;

    /**
     * 处理人
     */
    private Long handlerId;

    /**
     * 处理人名称
     */
    private String handlerName;

    /**
     * 审核失败原因
     */
    private Long auditMsgId;
}

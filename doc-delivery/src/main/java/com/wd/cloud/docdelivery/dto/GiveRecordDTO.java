package com.wd.cloud.docdelivery.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
@Data
@Accessors(chain = true)
public class GiveRecordDTO {

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
     * 2：用户应助，
     * 3：第三方应助,
     * 4:其它
     */
    private Integer giverType;

    /**
     * 0：待审核，1：审核通过，2：审核不通过，4：待上传
     */
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
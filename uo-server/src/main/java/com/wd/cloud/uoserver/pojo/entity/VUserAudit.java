package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 17:34
 * @Description:
 */
@Data
@Accessors(chain = true)
@Immutable
@Entity
@Table(name = "v_user_audit")
public class VUserAudit  extends AbstractEntity {

    @Id
    private Long id;
    private String username;
    private String email;
    private String nickname;

    private String orgFlag;
    /**机构名称*/
    private String orgName;

    /**院系名称*/
    private String departmentName;

    /**身份类型 1:学生，2：老师*/
    private Integer identityType;

    /**职工号/学号*/
    private String studentId;

    /**教育程度*/
    private Integer eduLevel;

    /**性别*/
    private Short sex;

    /**入学年份*/
    private String entranceYear;

    /**
     * 证件照
     */
    private String idPhoto;

    /**
     * 状态 0待审核，1审核不通过，2审核通过
     */
    private Integer status;

    /**
     * 操作人
     */
    private String handlerName;

    /**
     * 失败原因
     */
    private String remark;
}

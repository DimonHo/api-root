package com.wd.cloud.uoserver.pojo.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构院系
 */
@Data
@Accessors(chain = true)
public class OrgDeptDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String name;

    private String orgFlag;

    private String orgName;

    /**
     * 用户数量
     */
    private Long userCount;

}

package com.wd.cloud.commons.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DepartmentDTO implements Serializable {
    private Long id;

    private String name;

    private Long orgId;

    private String orgName;

    private Integer number;
}

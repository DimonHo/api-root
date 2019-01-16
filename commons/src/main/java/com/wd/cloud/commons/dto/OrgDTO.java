package com.wd.cloud.commons.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgDTO implements Serializable {
    private Long id;
    private String name;
    /**
     * 机构默认标识
     */
    private String flag;

    /**
     * 兼容spis机构标识
     */
    private String spisFlag;

    /**
     * 兼容智汇云，学科机构标识
     */
    private String eduFlag;
    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;
    private List<IpRangeDTO> ipRanges;
    private boolean enabled;
}

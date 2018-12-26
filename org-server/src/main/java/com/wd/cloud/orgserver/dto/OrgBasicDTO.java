package com.wd.cloud.orgserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgBasicDTO {

    private Long id;
    /**
     * 机构名称
     */
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

    /**
     * 是否激活使用0：否，1：是
     */
    private boolean enabled;

    private List<IpRangDTO> ipRang = new ArrayList<>();

}

package com.wd.cloud.commons.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date gmtCreate;
    private Date gmtModified;
    private String name;
    /**
     * 机构默认标识
     */
    private String flag;

    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;

    /**
     * Ip范围
     */
    private List<IpRangeDTO> ipRanges;
    /**
     * 机构产品
     */
    private List<ProductDTO> products;
    /**
     * 机构院系
     */
    private List<DepartmentDTO> departments;
    /**
     * 机构馆藏
     */
    private List<OrgCdbDTO> cdbs;
    /**
     * 机构联系人
     */
    private List<LinkmanDTO> linkmans;
    /**
     * 是否激活
     */
    private Boolean enabled;
}

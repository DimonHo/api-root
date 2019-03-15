package com.wd.cloud.uoserver.dto;

import com.wd.cloud.commons.dto.IpRangeDTO;
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

    private Date gmtCreate;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     *联系方式
     */
    private String contact;

    /**
     *邮箱
     */
    private String email;

    /**
     * 市、区
     */
    private String city;
    private List<IpRangeDTO> ipRanges;
    private List<ProductDTO> products;
    private boolean enabled;
}

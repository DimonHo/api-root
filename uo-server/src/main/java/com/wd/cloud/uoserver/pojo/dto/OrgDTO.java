package com.wd.cloud.uoserver.pojo.dto;

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

    private Long id;
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
    private List<OrgIpDTO> ipList;
    /**
     * 机构产品
     */
    private List<OrgProdDTO> prodList;
    /**
     * 机构院系
     */
    private List<OrgDeptDTO> deptList;
    /**
     * 机构馆藏
     */
    private List<OrgCdbDTO> cdbList;
    /**
     * 机构联系人
     */
    private List<OrgLinkmanDTO> linkmanList;
    /**
     * 是否激活
     */
    private Boolean disable;
}

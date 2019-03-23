package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 21:37
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgVO {
    /**
     * 机构默认标识
     */
    private String flag;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 省份
     */
    private String province;

    /**
     * 市、区
     */
    private String city;

    List<OrgLinkmanVO> linkman;

    List<OrgProductVO> product;

    List<OrgIpVO> ip;

    List<OrgCdbVO> cdbs;
}

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
    List<OrgLinkmanVO> linkman;
    List<OrgProdVO> prod;
    List<OrgIpVO> ip;
    List<OrgCdbVO> cdb;
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
    /**
     * 是否禁用
     */
    private Boolean disable;
}

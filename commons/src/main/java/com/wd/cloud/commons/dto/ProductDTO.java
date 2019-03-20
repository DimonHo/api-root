package com.wd.cloud.commons.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 产品访问地址
     */
    private String url;
    /**
     * 生效时间
     */
    private Date effDate;
    /**
     * 失效时间
     */
    private Date expDate;
    /**
     * 产品状态： 购买、试用、停用
     */
    private Integer status;
    /**
     * 是否单独购买
     */
    private Boolean single;

    private String orgFlag;

}

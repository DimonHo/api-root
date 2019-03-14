package com.wd.cloud.uoserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class ProductDTO {
    private Long id;
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
    private Date beginTime;
    /**
     * 失效时间
     */
    private Date endTime;
    /**
     * 产品状态： 购买、试用、停用
     */
    private Integer status;
    /**
     * 是否单独购买
     */
    private Boolean single;
}

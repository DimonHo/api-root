package com.wd.cloud.uoserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class OrgProductDTO {
    //机构名称
    private String orgName;

    //产品ID
    private Long productId;

    //产品名称
    private String productName;

    //状态  1购买、2试用、0停用
    private Integer status;

    //注册时间
    private Date gmtCreate;

}

package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 12:03
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgProductVO {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品状态
     */
    @Max(value = 2)
    @Min(value = 0)
    private Integer status;

    private Date effDate;

    private Date exfDate;

    private Boolean single;

    private Boolean del;
}

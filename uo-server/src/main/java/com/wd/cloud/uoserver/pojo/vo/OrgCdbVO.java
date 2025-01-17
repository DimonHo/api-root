package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 10:43
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OrgCdbVO {

    private Long id;

    private String name;

    private String url;

    private String localUrl;

    private Integer type;

    private Boolean display;

    private Boolean del;
}

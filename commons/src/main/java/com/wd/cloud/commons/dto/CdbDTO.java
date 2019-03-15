package com.wd.cloud.commons.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CdbDTO implements Serializable {
    private Long id;

    /**馆藏名称*/
    private String name;

    /**馆藏url*/
    private String url;

    /**机构标示*/
    private String flag;

    /**本地资源url*/
    private String localUrl;

    /**是否显示馆藏数据库*/
    private boolean display;

    /**学校名称*/
    private String orgName;
}

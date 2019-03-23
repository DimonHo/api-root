package com.wd.cloud.uoserver.pojo.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构馆藏
 */

@Data
@Accessors(chain = true)
public class OrgCdbDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;

    /**馆藏名称*/
    private String name;

    /**馆藏url*/
    private String url;

    /**机构标示*/
    private String orgFlag;

    private String orgName;

    /**本地资源url*/
    private String localUrl;

    /**是否显示馆藏数据库*/
    private Boolean display;

}

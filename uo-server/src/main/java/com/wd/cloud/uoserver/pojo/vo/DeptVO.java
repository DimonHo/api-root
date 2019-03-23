package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 11:27
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeptVO {

    private Long id;
    /**
     * 所属上级院系
     */
    private Long pid;

    /**
     * 院系名称
     */
    private String name;

    boolean del;
}

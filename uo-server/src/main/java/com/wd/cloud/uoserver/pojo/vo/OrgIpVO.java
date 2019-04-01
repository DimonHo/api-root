package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 16:13
 * @Description: 机构IP模型
 */
@Data
@Accessors(chain = true)
public class OrgIpVO {

    /**
     * 删除或更新id不能为空
     */
    private Long id;
    /**
     * 开始IP
     */
    private String begin;
    /**
     * 结束IP
     */
    private String end;
    /**
     * 是否删除？
     */
    private boolean del;
}

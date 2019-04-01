package com.wd.cloud.uoserver.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/24 12:46
 * @Description: 权限对象
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户权限参数对象")
public class PermissionVO {

    @NotNull
    private String username;

    /**
     * 权限类型， 1：校外访问
     */
    @NotNull
    @ApiModelProperty(value = "权限类型", example = "1 表示校外权限")
    private Integer type;

    /**
     * 权限值
     */
    @ApiModelProperty(value = "权限值", example = "1：表示6个月，2：永久")
    private Integer value;
    /**
     * 生效时间
     */
    @ApiModelProperty(value = "生效时间", example = "2019-01-01 00:00:00")
    private Date effDate;
    /**
     * 失效时间
     */
    @ApiModelProperty(value = "失效时间", example = "2099-01-01 00:00:00")
    private Date expDate;

    /**
     * 是否是删除操作
     */
    @ApiModelProperty(value = "是否是删除", example = "false or true")
    private Boolean del;
}

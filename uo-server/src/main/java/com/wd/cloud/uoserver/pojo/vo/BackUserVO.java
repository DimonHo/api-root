package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 18:45
 * @Description:
 */
@Data
@Accessors(chain = true)
public class BackUserVO {

    private String username;
    private String password;
    private String userType;
    private String nickname;
    private String realname;

    private String email;

    private String orgFlag;

    private String orgName;
    private String phone;

    /**院系ID*/
    private String departmentId;

    /**身份类型 1:学生，2：老师*/
    private Integer identityType;

    /**职工号/学号*/
    private String studentId;

    /**教育程度*/
    private Integer eduLevel;

    /**性别*/
    private Short sex;

    /**入学年份*/
    private String entranceYear;

}

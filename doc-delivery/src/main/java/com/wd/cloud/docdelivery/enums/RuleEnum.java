package com.wd.cloud.docdelivery.enums;

/**
 * @author He Zhigang
 * @date 2018/12/21
 * @Description:
 */
public enum RuleEnum {
    /**
     * 校内用户
     */
    IS_INNER("校内", 1),
    /**
     * 登陆用户
     */
    IS_LOGIN("已登录", 2),
    /**
     * 已验证身份
     */
    IS_VALIDATE("已验证", 4);

    private String name;
    private int value;

    RuleEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int value() {
        return value;
    }
}

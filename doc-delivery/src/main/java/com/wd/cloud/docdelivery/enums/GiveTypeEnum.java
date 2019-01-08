package com.wd.cloud.docdelivery.enums;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description:
 */
public enum GiveTypeEnum {

    /**
     * 应助者类型
     */
    AUTO("系统自动应助", 0),
    MANAGER("管理员应助", 1),
    USER("用户应助", 2),
    BIG_DB("数据库全文", 3);

    private String name;
    private int code;

    private GiveTypeEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}

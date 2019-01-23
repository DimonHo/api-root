package com.wd.cloud.docdelivery.enums;

/**
 * @author He Zhigang
 * @date 2018/5/22
 * @Description:
 */
public enum AuditEnum {

    WAIT("待审核", 0),
    PASS("审核通过", 1),
    NO_PASS("审核不通过", 2),
    WAIT_UPLOAD("待上传", 4);

    private String name;
    private int value;

    private AuditEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int value() {
        return value;
    }

}

package com.wd.cloud.uoserver.enums;

public enum AuditEnum {
    /**
     * 待审核
     */
    WAITE("待审核", 0),
    NO_PASS("审核不通过", 1),
    PASS("审核通过", 2);

    private String name;
    private int value;

    private AuditEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String name(int value) {
        for (AuditEnum auditIdCardEnums : AuditEnum.values()) {
            if (auditIdCardEnums.value() == value) {
                return auditIdCardEnums.name();
            }
        }
        return null;
    }

    public static AuditEnum match(int value) {
        for (AuditEnum auditIdCardEnums : AuditEnum.values()) {
            if (auditIdCardEnums.value() == value) {
                return auditIdCardEnums;
            }
        }
        return null;
    }

    public Integer value() {
        return value;
    }
}

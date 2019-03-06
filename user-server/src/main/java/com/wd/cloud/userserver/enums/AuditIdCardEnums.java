package com.wd.cloud.userserver.enums;

public enum AuditIdCardEnums {
    AUDIE_AUDIT("待审核", 0),
    AUDIE_NO_PASS("审核不通过", 1),
    AUDIE_PASS("审核通过",2);









    private String name;
    private int value;

    private AuditIdCardEnums(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String name(int value) {
        for (AuditIdCardEnums auditIdCardEnums : AuditIdCardEnums.values()) {
            if (auditIdCardEnums.value() == value) {
                return auditIdCardEnums.name();
            }
        }
        return null;
    }

    public static AuditIdCardEnums match(int value){
        for (AuditIdCardEnums auditIdCardEnums : AuditIdCardEnums.values()) {
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

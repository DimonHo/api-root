package com.wd.cloud.uoserver.enums;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 15:16
 * @Description: 权限类型
 */
public enum PermissionTypeEnum {

    /**
     * 权限类型
     */
    OUTSIDE("校外访问权限",1);

    private String name;
    private int value;

    private PermissionTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String name(int value) {
        for (PermissionTypeEnum permissionTypeEnum : PermissionTypeEnum.values()) {
            if (permissionTypeEnum.value() == value) {
                return permissionTypeEnum.name();
            }
        }
        return null;
    }

    public static PermissionTypeEnum match(int value) {
        for (PermissionTypeEnum permissionTypeEnum : PermissionTypeEnum.values()) {
            if (permissionTypeEnum.value() == value) {
                return permissionTypeEnum;
            }
        }
        return null;
    }

    public Integer value() {
        return value;
    }
}

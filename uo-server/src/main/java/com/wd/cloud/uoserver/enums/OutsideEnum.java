package com.wd.cloud.uoserver.enums;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 15:33
 * @Description: 校外权限
 */
public enum  OutsideEnum {
    /**
     * 校外权限状态
     */
    HALF_YEAR("6个月校外权限",1),
    FOREVER("永久校外权限",2);


    private String name;
    private int value;

    private OutsideEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String name(int value) {
        for (OutsideEnum outsideEnum : OutsideEnum.values()) {
            if (outsideEnum.value() == value) {
                return outsideEnum.name();
            }
        }
        return null;
    }

    public static OutsideEnum match(int value) {
        for (OutsideEnum outsideEnum : OutsideEnum.values()) {
            if (outsideEnum.value() == value) {
                return outsideEnum;
            }
        }
        return null;
    }

    public Integer value() {
        return value;
    }
}

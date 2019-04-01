package com.wd.cloud.uoserver.enums;

public enum ProdStatusEnum {

    /**
     * 产品状态
     */
    TRIAL("试用",1),
    BUY("购买",2),
    STOP("停用",3);

    private String name;
    private int value;

    private ProdStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String name(int value) {
        for (ProdStatusEnum prodStatusEnum : ProdStatusEnum.values()) {
            if (prodStatusEnum.value() == value) {
                return prodStatusEnum.name();
            }
        }
        return null;
    }

    public static ProdStatusEnum match(int value) {
        for (ProdStatusEnum prodStatusEnum : ProdStatusEnum.values()) {
            if (prodStatusEnum.value() == value) {
                return prodStatusEnum;
            }
        }
        return null;
    }

    public Integer value() {
        return value;
    }
}

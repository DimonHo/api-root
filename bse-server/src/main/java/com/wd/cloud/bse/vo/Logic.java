package com.wd.cloud.bse.vo;

public enum Logic {

    AND(1), OR(2), NOT(3);

    private int value;

    Logic(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}

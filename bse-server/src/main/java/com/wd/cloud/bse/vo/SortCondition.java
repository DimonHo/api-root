package com.wd.cloud.bse.vo;

/**
 * 排序
 *
 * @author Administrator
 */
public class SortCondition {

    private String field;

    private String nested;

    private String mode;

    private int orderKey = 1;

    public SortCondition(String field, int orderKey) {
        this.field = field;
        this.orderKey = orderKey;
    }

    public SortCondition(String field, String nested, int orderKey) {
        this.field = field;
        this.nested = nested;
        this.orderKey = orderKey;
    }

    public SortCondition(String field, String nested, String mode, int orderKey) {
        this.field = field;
        this.nested = nested;
        this.mode = mode;
        this.orderKey = orderKey;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getNested() {
        return nested;
    }

    public void setNested(String nested) {
        this.nested = nested;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(int orderKey) {
        this.orderKey = orderKey;
    }


}

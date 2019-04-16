package com.wd.cloud.bse.vo;


import java.util.List;

public class Pager<T> {

    private int total;

    private int count;

    private List<T> rows;

    public Pager() {
    }

    public Pager(int total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

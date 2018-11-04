package com.wd.cloud.fsserver.model;


/**
 * @author He Zhigang
 * @date 2018/8/23
 * @Description:
 */
public class TableModel {
    private String tableName;
    /**
     * 使用文件unid作为rowkey
     */
    private byte[] rowKey;
    private byte[] family = "cf".getBytes();
    private byte[] qualifier = "fileByte".getBytes();
    /**
     * 文件byte流
     */
    private byte[] value;

    private String fileName;

    public TableModel() {
    }

    public TableModel(String tableName, byte[] rowKey, byte[] value) {
        this.tableName = tableName;
        this.rowKey = rowKey;
        this.value = value;
    }


    public TableModel(String tableName, byte[] rowKey, byte[] family, byte[] qualifier, byte[] value) {
        this.tableName = tableName;
        this.rowKey = rowKey;
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
    }


    public static TableModel create() {
        return new TableModel();
    }

    public String getTableName() {
        return tableName;
    }

    public TableModel setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public TableModel setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public byte[] getRowKey() {
        return rowKey;
    }

    public TableModel setRowKey(byte[] rowKey) {
        this.rowKey = rowKey;
        return this;
    }

    public byte[] getFamily() {
        return family;
    }

    public TableModel setFamily(byte[] family) {
        this.family = family;
        return this;
    }

    public byte[] getQualifier() {
        return qualifier;
    }

    public TableModel setQualifier(byte[] qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    public byte[] getValue() {
        return value;
    }

    public TableModel setValue(byte[] value) {
        this.value = value;
        return this;
    }
}

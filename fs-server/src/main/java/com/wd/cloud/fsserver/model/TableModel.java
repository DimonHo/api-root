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
    private String rowKey;
    private String family = "cf";
    private String qualifier = "fileByte";
    /**
     * 文件byte流
     */
    private byte[] value;

    private String fileName;

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

    public String getRowKey() {
        return rowKey;
    }

    public TableModel setRowKey(String rowKey) {
        this.rowKey = rowKey;
        return this;
    }

    public String getFamily() {
        return family;
    }

    public TableModel setFamily(String family) {
        this.family = family;
        return this;
    }

    public String getQualifier() {
        return qualifier;
    }

    public TableModel setQualifier(String qualifier) {
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

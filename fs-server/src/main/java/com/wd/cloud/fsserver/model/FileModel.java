package com.wd.cloud.fsserver.model;

/**
 * @author He Zhigang
 * @date 2018/12/7
 * @Description:
 */
public class FileModel {
    private String name;
    private byte[] bytes;

    public String getName() {
        return name;
    }

    public FileModel setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FileModel setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }
}

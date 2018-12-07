package com.wd.cloud.fsserver.service;

import com.wd.cloud.fsserver.model.FileModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/10/31
 * @Description:
 */
public interface HbaseService {

    void asFileFromHbase(String tableName);

    boolean saveToHbase(String tableName, MultipartFile file) throws Exception;

    boolean saveToHbase(String tableName, File file);

    boolean saveToHbase(String tableName, String rowKey, byte[] fileByte);

    FileModel getFileFromHbase(String tableName, String rowKey);

    /**
     * 删除hbase记录
     *
     * @param tableName
     * @param rowKey
     * @return
     */
    boolean deleteFileFromHbase(String tableName, String rowKey);

    /**
     * 同步hbase文件到upload记录，并更新hbase文件
     *
     * @param tableName
     * @return
     */
    int hfToUploadRecord(String tableName);

    void dropTable(String tableName) throws IOException;

    void createTable(String tableName) throws IOException;
}

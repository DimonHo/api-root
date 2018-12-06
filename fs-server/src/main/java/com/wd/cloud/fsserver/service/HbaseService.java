package com.wd.cloud.fsserver.service;

import com.wd.cloud.fsserver.model.TableModel;
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

    boolean saveToHbase(String tableName, String md5, MultipartFile file) throws Exception;

    boolean saveToHbase(String tableName, String md5, File file) throws Exception;

    boolean saveToHbase(String tableName, String md5, byte[] fileByte, String fileName) throws Exception;

    boolean saveToHbase(String tableName, File file) throws Exception;

    boolean saveToHbase(TableModel tableModel) throws Exception;

    byte[] getFileFromHbase(String tableName, String md5);

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

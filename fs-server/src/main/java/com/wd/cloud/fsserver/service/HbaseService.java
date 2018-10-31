package com.wd.cloud.fsserver.service;

import com.wd.cloud.fsserver.model.HbaseObjModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author He Zhigang
 * @date 2018/10/31
 * @Description:
 */
public interface HbaseService {

    void saveToHbase(String tableName, String unid, MultipartFile file) throws Exception;

    void saveToHbase(String tableName, String unid, File file) throws Exception;

    void saveToHbase(String tableName, File file) throws Exception;

    void saveToHbase(HbaseObjModel hbaseObjModel) throws Exception;

    byte[] getFileFromHbase(String tableName, String unid);
}

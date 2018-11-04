package com.wd.cloud.fsserver.service;

import com.wd.cloud.fsserver.entity.UploadRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author He Zhigang
 * @date 2018/7/20
 * @Description:
 */
public interface FileService {

    UploadRecord save(String dir, MultipartFile file) throws Exception;

    File getFile(String unid);


}

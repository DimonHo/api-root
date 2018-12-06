package com.wd.cloud.commons.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author He Zhigang
 * @date 2018/12/5
 * @Description:
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    public static String fileMd5(File file) throws IORuntimeException {
        return fileMd5(FileUtil.getInputStream(file));
    }

    /**
     * 计算文件MD5码
     *
     * @param fileStream
     * @return
     */
    public static String fileMd5(InputStream fileStream) {
        String fileMd5 = DigestUtil.md5Hex(fileStream);
        IoUtil.close(fileStream);
        return fileMd5;
    }
}

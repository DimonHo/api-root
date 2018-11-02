package com.wd.cloud.fsserver.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author He Zhigang
 * @date 2018/8/23
 * @Description:
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    private static final Log log = LogFactory.get();

    public static String fileMd5(File file) throws IORuntimeException {
        return fileMd5(FileUtil.getInputStream(file));
    }

    public static String fileMd5(MultipartFile file) throws IOException {
        return fileMd5(file.getInputStream());
    }

    public static String fileMd5(InputStream fileIo) {
        log.info("开始计算{}文件的MD5...");
        String fileMd5 = DigestUtil.md5Hex(fileIo);
        log.info("文件{}的MD5计算完成。");
        return fileMd5;
    }

    /**
     * 生成文件uuid码
     *
     * @param path
     * @param fileMd5
     * @return
     */
    public static String buildFileUuid(String path, String fileMd5) {
        return SecureUtil.md5(fileMd5 + path);
    }

    /**
     * 获取文件后缀名
     * 如果文件名不带后缀，则从文件头中获取
     *
     * @param fileName
     * @param file
     * @return
     */
    public static String getFileType(String fileName, MultipartFile file) {
        String ext = FileUtil.extName(fileName).trim().toLowerCase();
        try {
            ext = StrUtil.isBlank(ext) ? FileTypeUtil.getType(file.getInputStream()) : ext;
        } catch (IOException e) {
            ext = StrUtil.EMPTY;
        }
        return ext;
    }

    public static String getFileType(String fileName, File file) {
        String ext = FileUtil.extName(fileName).trim().toLowerCase();
        try {
            ext = StrUtil.isBlank(ext) ? FileTypeUtil.getType(file) : ext;
        } catch (Exception e) {
            ext = StrUtil.EMPTY;
        }
        return ext;
    }

    public static String buildFileName(String fileName,File file){
        String ext = FileUtil.extName(fileName).trim().toLowerCase();
        String fileType = FileTypeUtil.getType(file);
        fileName = StrUtil.isBlank(ext) && !StrUtil.isBlank(fileType) ? fileName + "."+ fileType : fileName;
        return fileName;
    }

    public static File saveToDisk(String absolutePath, String fileName, MultipartFile file) throws IOException {
        File newFile = new File(absolutePath, fileName);
        if (!FileUtil.exist(newFile)) {
            log.info("正在保存{}文件...",absolutePath + fileName);
            //将文件流写入文件中
            newFile = FileUtil.writeFromStream(file.getInputStream(), newFile);
            log.info("文件{}已保存成功。",absolutePath + fileName);
        }
        log.info("文件{}已存在。", absolutePath + fileName);
        return newFile;
    }

    public static File saveToDisk(String absolutePath, String fileName, byte[] fileByte) throws IOException {
        File newFile = new File(absolutePath, fileName);
        if (!FileUtil.exist(newFile)) {
            log.info("正在保存{}文件...",absolutePath + fileName);
            //将文件流写入文件中
            newFile = FileUtil.writeBytes(fileByte, newFile);
            log.info("文件{}已保存成功。",absolutePath + fileName);
        }
        log.info("文件{}已存在。", absolutePath + fileName);
        return newFile;
    }

    public static File getFileFromDisk(String absolutePath, String fileName) {
        return new File(absolutePath, fileName);
    }

}

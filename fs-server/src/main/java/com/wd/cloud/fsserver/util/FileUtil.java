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

    /**
     * 计算文件MD5码
     *
     * @param fileStream
     * @return
     */
    public static String fileMd5(InputStream fileStream) {
        log.info("开始计算文件的MD5...");
        String fileMd5 = DigestUtil.md5Hex(fileStream);
        log.info("文件的MD5计算完成。");
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
        //MD5在前，防止以unid作为hbase的rowKey产生热点问题
        return SecureUtil.md5(fileMd5 + path);
    }

    /**
     * 获取文件后缀名
     * 如果文件名不带后缀，则从文件头中获取
     *
     * @param file
     * @return
     */
    public static String getFileType(MultipartFile file) {
        String ext = FileUtil.extName(file.getOriginalFilename()).trim().toLowerCase();
        try {
            ext = StrUtil.isBlank(ext) ? FileTypeUtil.getType(file.getInputStream()) : ext;
        } catch (IOException e) {
            ext = StrUtil.EMPTY;
        }
        return ext;
    }

    /**
     * 获取文件类型，如果文件名没带后缀名，则读取文件头判断
     *
     * @param file
     * @return
     */
    public static String getFileType(File file) {
        String ext = FileUtil.extName(file.getName()).trim();
        try {
            ext = StrUtil.isBlank(ext) ? FileTypeUtil.getType(file) : ext;
        } catch (Exception e) {
            ext = StrUtil.EMPTY;
        }
        return ext;
    }

    public static String buildFileName(String md5, String fileType) {
        return String.format("%s.%s", md5, fileType);
    }

    /**
     * 自动补全文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileName(File file) {
        String ext = FileUtil.extName(file.getName()).trim();
        String fileType = FileTypeUtil.getType(file);
        return StrUtil.isBlank(ext) && !StrUtil.isBlank(fileType) ? file.getName() + "." + fileType : file.getName();
    }

    /**
     * 自动补全文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(File file, String md5) {
        String ext = FileUtil.getFileType(file);
        return md5 + "." + ext;
    }

    /**
     * 自动补全MD5文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(MultipartFile file, String md5) {
        String ext = FileUtil.getFileType(file);
        return md5 + "." + ext;
    }


    /**
     * 自动补全MD5文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(File file) {
        return buildFileMd5Name(file, FileUtil.fileMd5(file));
    }

    /**
     * 自动补全MD5文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(MultipartFile file) throws IOException {
        return buildFileMd5Name(file, FileUtil.fileMd5(file));
    }

    /**
     * 保存文件到磁盘目录,以MD5命名
     *
     * @param absolutePath 文件绝对路径
     * @param file
     * @param md5
     * @return
     * @throws IOException
     */
    public static File saveToDisk(String absolutePath, MultipartFile file, String md5) throws IOException {
        String fileMd5Name = FileUtil.buildFileMd5Name(file, md5);
        return saveToDisk(absolutePath, fileMd5Name, file.getBytes());
    }

    public static File saveToDisk(String absolutePath, MultipartFile file) throws IOException {
        String fileMd5Name = FileUtil.buildFileMd5Name(file);
        return saveToDisk(absolutePath, fileMd5Name, file.getBytes());
    }

    public static File saveToDisk(String absolutePath, String fileName, byte[] fileByte) throws IOException {
        File newFile = new File(absolutePath, fileName);
        // 文件不存在
        if (!FileUtil.exist(newFile)) {
            log.info("正在保存{}文件...", newFile.getName());
            //将文件流写入文件中
            newFile = FileUtil.writeBytes(fileByte, newFile);
            log.info("文件{}已保存成功。", newFile.getName());
            //如果存在同名文件，但内容不同
        } else {
            String fileMd5 = FileUtil.fileMd5(newFile);
            //如果文件名于MD5不匹配，则用MD5重命名该文件
            if (!fileMd5.equals(getFileName(newFile, true))) {
                FileUtil.rename(newFile, fileMd5, true, true);
                // 重新保存这个文件
                return saveToDisk(absolutePath, fileName, fileByte);
            }
            log.info("文件{}已存在。", newFile.getName());
        }
        return newFile;
    }


    public static File getFileFromDisk(String absolutePath, String fileName) {
        return new File(absolutePath, fileName);
    }

    /**
     * 获取文件名称
     *
     * @param file      文件
     * @param ignoreExt 是否排除文件后缀
     * @return
     */
    public static String getFileName(File file, boolean ignoreExt) {
        String fileName = file.getName();
        return getFileName(fileName, ignoreExt);
    }

    public static String getFileName(String fileName, boolean ignoreExt) {
        if (ignoreExt) {
            return StrUtil.subBefore(fileName, ".", true);
        } else {
            return fileName;
        }
    }
}

package com.wd.cloud.fsserver.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
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
        long start = System.currentTimeMillis();
        String fileMd5 = DigestUtil.md5Hex(fileStream);
        log.info("计算MD5耗时：{}毫秒",DateUtil.spendMs(start));
        IoUtil.close(fileStream);
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
        return SecureUtil.md5(path + fileMd5);
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
        InputStream fileStream = null;
        try {
            fileStream = file.getInputStream();
            ext = StrUtil.isBlank(ext) ? FileTypeUtil.getType(fileStream) : ext;
        } catch (IOException e) {
            ext = StrUtil.EMPTY;
        } finally {
            IoUtil.close(fileStream);
        }
        return ext.toLowerCase();
    }

    /**
     * 获取文件类型，如果文件名没带后缀名，则读取文件头判断
     *
     * @param file
     * @return
     */
    public static String getFileType(File file) {
        String ext = FileUtil.extName(file.getName()).trim();
        String type = FileUtil.getType(file);
        try {
            ext = StrUtil.isBlank(ext) && !StrUtil.isBlank(type) ? type : ext;
        } catch (Exception e) {
            ext = StrUtil.EMPTY;
        }
        return ext.toLowerCase();
    }

    public static String buildFileName(String md5, String fileType) {
        if (StrUtil.isBlank(fileType)){
            return md5;
        }
        return String.format("%s.%s", md5, fileType.toLowerCase());
    }

    /**
     * 自动补全文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileName(File file) {
        String ext = FileUtil.extName(file.getName()).trim();
        String type = FileTypeUtil.getType(file);
        return StrUtil.isBlank(ext) && !StrUtil.isBlank(type) ? file.getName() + "." + type : file.getName();
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
     * 自动补全文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(File file, String md5) {
        String ext = FileUtil.getFileType(file);
        return !StrUtil.isBlank(ext) ? md5 + "." + ext : md5;
    }

    /**
     * 自动补全MD5文件名后缀
     *
     * @param file
     * @return
     */
    public static String buildFileMd5Name(MultipartFile file, String md5) {
        String ext = FileUtil.getFileType(file);
        return !StrUtil.isBlank(ext) ? md5 + "." + ext : md5;
    }

    public static File saveToDisk(String absolutePath, MultipartFile file) throws IOException {
        String md5 = FileUtil.fileMd5(file);
        return saveToDisk(absolutePath, file, md5);
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

    public static File saveToDisk(String absolutePath, String fileName, byte[] fileByte) throws IOException {
        File file = new File(absolutePath, fileName);
        // 文件不存在
        if (!FileUtil.exist(file)) {
            log.info("正在保存{}文件...", file.getName());
            //将文件流写入文件中
            File newFile = FileUtil.writeBytes(fileByte, file);
            log.info("文件{}已保存成功。", newFile.getName());
            return newFile;
            //如果存在同名文件，但内容不同
        } else {
            String beforeName = file.getName();
            log.info("文件{}已存在。", beforeName);
            String fileMd5 = FileUtil.fileMd5(file);
            //如果文件名于MD5不匹配，则用MD5重命名该文件
            if (!fileMd5.equals(getFileName(file, true))) {
                log.info("正在重命名文件:{}",beforeName);
                File renameFile = FileUtil.rename(file, fileMd5, true, true);
                String afterName = renameFile.getName();
                log.info("重命名文件成功:{} --> {}",beforeName,afterName);
                // 重新保存这个文件
                return saveToDisk(absolutePath, fileName, fileByte);
            }
            return file;
        }
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

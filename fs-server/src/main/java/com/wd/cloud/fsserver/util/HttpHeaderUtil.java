package com.wd.cloud.fsserver.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * @author He Zhigang
 * @date 2018/8/24
 * @Description:
 */
public class HttpHeaderUtil {

    /**
     * 浏览器文件下载httpHeader构建
     *
     * @param fileName
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static HttpHeaders buildBroserFileHttpHeaders(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        //判断是否是IE浏览器，对文件名进行编码，防止乱码
        if (request.getHeader("user-agent").toLowerCase().contains("msie")) {
            fileName = URLUtil.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
        }
        HttpHeaders headers = new HttpHeaders();
        String disposition = StrUtil.format("attachment; filename=\"{}\"", fileName);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", disposition);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

    /**
     * API文件下载httpHeader构建
     *
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static HttpHeaders buildApiFileHttpHeaders(String fileName) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        String disposition = StrUtil.format("attachment; filename=\"{}\"", fileName);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", disposition);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }
}

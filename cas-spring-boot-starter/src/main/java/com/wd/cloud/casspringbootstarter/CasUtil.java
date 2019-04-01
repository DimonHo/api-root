package com.wd.cloud.casspringbootstarter;

import cn.hutool.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/2/23
 * @Description:
 */
public class CasUtil {

    /**
     * 获取ST
     *
     * @param request
     * @param casServerLoginUrl
     * @param clientUrl
     * @param tgcCookie
     * @return
     */
    public static String getSt(HttpServletRequest request, String casServerLoginUrl, String clientUrl, String tgcCookie) {
        Map<String, Object> params = new HashMap<>();
        params.put("method", "POST");
        params.put("service", clientUrl);
        String stHtml = HttpRequest.get(casServerLoginUrl)
                .header("User-Agent", request.getHeader("User-Agent"))
                .cookie(tgcCookie)
                .form(params)
                .execute()
                .body();
        Document stDoc = Jsoup.parse(stHtml);
        return stDoc.getElementsByAttributeValue("name", "ticket").attr("value");
    }
}

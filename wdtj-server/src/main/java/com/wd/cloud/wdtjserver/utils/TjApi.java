package com.wd.cloud.wdtjserver.utils;

import cn.hutool.json.JSONObject;
import com.wd.cloud.wdtjserver.config.BaiduTjConfig;
import org.springframework.beans.factory.annotation.Autowired;


public class TjApi {

    @Autowired
    BaiduTjConfig baiduTjConfig;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            JSONObject header = new JSONObject();
            header.put("username","");//用户名
            header.put("password", "");//用户密码
            header.put("token", "");//申请到的token
            header.put("account_type", "1");

            String urlStr = "https://api.baidu.com/json/tongji/v1/ReportService/getSiteList";
            String charset = "utf-8";

            JSONObject params = new JSONObject();
            params.put("header", header);

            byte[] res = HttpsUtil.post(urlStr, params.toString(), charset);
            String s = new String(res);
            System.out.println(s);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

}

package com.wd.cloud.wdtjserver.utils;

import cn.hutool.json.JSONObject;



public class TjApiDate {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            JSONObject header = new JSONObject();
            header.put("username", "wdxxkj2014");//用户名
            header.put("password", "hr@hnwdkj.com");//用户密码
            header.put("token", "bcb491d69976e38bb84793606ec7169e");//申请到的token
            header.put("account_type", "1");

//          String urlStr = "https://api.baidu.com/json/tongji/v1/ReportService/getSiteList";
            String urlStr = "https://api.baidu.com/json/tongji/v1/ReportService/getData";
            String charset = "utf-8";


            JSONObject body = new JSONObject();
            body.put("siteId","5780156");
            body.put("method","visit/topdomain/a");//需要获取的数据
            body.put("start_date","20150603");
            body.put("end_date","20171222");
            body.put("metrics","pv_count,pv_ratio,visit_count");//指标,数据单位

            JSONObject params = new JSONObject();
            params.put("header", header);
            params.put("body", body);

            byte[] res = HttpsUtil.post(urlStr, params.toString(), charset);
            String s = new String(res);
            System.out.println(s);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

}

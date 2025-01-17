package com.wd.cloud.reportanalysis.service.impl;

import com.wd.cloud.reportanalysis.service.CxfWebServiceI;
import com.wd.cloud.reportanalysis.util.WebserviceClient;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CxfWebService implements CxfWebServiceI {

    @Autowired
    private WebserviceClient webserviceClient;

    @Override
    public Map<String, Object> amount(String xml) {
        try {
            Object[] objects = new Object[0];
            objects = webserviceClient.getTransportClient().invoke("compare", xml);
            System.out.println("返回数据:" + objects[0]);
            JSONObject json = JSONObject.fromObject(objects[0]);
            Map map = (Map) json;
            return map;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

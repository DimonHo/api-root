package com.wd.cloud.docdelivery.feign;


import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "org-server", fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {
    @GetMapping("/getIpRang")
    ResponseModel<List<JSONObject>> getIpRang();


    @Component
    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel<List<JSONObject>> getIpRang() {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:org-server调用失败！");
        }
    }
}

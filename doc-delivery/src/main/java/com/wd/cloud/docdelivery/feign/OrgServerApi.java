package com.wd.cloud.docdelivery.feign;


import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "org-server", fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {

    @GetMapping("/orginfo/get")
    ResponseModel<JSONObject> getByIp(@RequestParam(value = "ip") String ip);

    @Component
    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel<JSONObject> getByIp(String ip) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:org-server调用失败！");
        }
    }
}

package com.wd.cloud.userserver.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
@FeignClient(value = "org-server", fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {

    @GetMapping("/org/{id}")
    public ResponseModel<JSONObject> getOrg(@PathVariable(value = "id") Long id);

    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel<JSONObject> getOrg(Long id) {
            return null;
        }
    }
}

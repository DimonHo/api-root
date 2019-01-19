package com.wd.cloud.apigateway.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
@FeignClient(value = "org-server", fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {

    @GetMapping("/org/get")
    public ResponseModel<OrgDTO> getByIp(@RequestParam(value = "ip") String ip);

    @GetMapping("/org/{id}")
    public ResponseModel<OrgDTO> getOrg(@PathVariable(value = "id") Long id);

    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel<OrgDTO> getOrg(Long id) {
            return null;
        }

        @Override
        public ResponseModel<OrgDTO> getByIp(String ip) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}

package com.wd.cloud.apigateway.feign;

import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2019/3/5
 * @Description:
 */
@FeignClient(value = "uo-server", fallback = UoServerApi.Fallback.class)
public interface UoServerApi {

    /**
     * 查询机构信息
     *
     * @param orgName
     * @param flag
     * @param spisFlag
     * @param eduFlag
     * @param ip
     * @return
     */
    @GetMapping("/org")
    ResponseModel<OrgDTO> getOrg(@RequestParam(value = "orgName", required = false) String orgName,
                                 @RequestParam(value = "flag", required = false) String flag,
                                 @RequestParam(value = "spisFlag", required = false) String spisFlag,
                                 @RequestParam(value = "eduFlag", required = false) String eduFlag,
                                 @RequestParam(value = "ip", required = false) String ip);

    @Component("uoServerApi")
    class Fallback implements UoServerApi {

        @Override
        public ResponseModel<OrgDTO> getOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip) {
            return ResponseModel.fail();
        }
    }
}

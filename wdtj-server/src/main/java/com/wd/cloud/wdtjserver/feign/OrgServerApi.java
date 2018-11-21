package com.wd.cloud.wdtjserver.feign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2018/11/21
 * @Description:
 */
@FeignClient(value = "org-server",
        fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {

    @GetMapping("/orginfo/all")
    ResponseModel getAll(@RequestParam(value = "sort", required = false, defaultValue = "name") String sort);

    @GetMapping("/orginfo/{id}")
    ResponseModel getOrg(@PathVariable(value = "id") Long id);


    @Component
    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel getAll(String sort) {
            return ResponseModel.fail().setMessage("fallback:机构服务调用失败！");
        }

        @Override
        public ResponseModel getOrg(Long id) {
            return ResponseModel.fail().setMessage("fallback:机构服务调用失败！");
        }
    }
}

package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
@FeignClient(value = "org-server",
        fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi {

    @GetMapping("/orginfo/all")
    public ResponseModel getAll(@RequestParam(value = "sort", required = false, defaultValue = "name") String sort);

    @GetMapping("/orginfo/{id}")
    public ResponseModel getOrg(@PathVariable(value = "id") Long id);


    class Fallback implements OrgServerApi {

        @Override
        public ResponseModel getAll(String sort) {
            return ResponseModel.fail();
        }

        @Override
        public ResponseModel getOrg(Long id) {
            return ResponseModel.fail();
        }
    }
}

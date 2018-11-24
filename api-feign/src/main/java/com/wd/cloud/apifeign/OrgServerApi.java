package com.wd.cloud.apifeign;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
@FeignClient(value = "org-server")
public interface OrgServerApi {

    @GetMapping("/orginfo/all")
    ResponseModel getAll(@RequestParam(value = "sort", required = false, defaultValue = "name") String sort);

    @GetMapping("/orginfo/{id}")
    ResponseModel getOrg(@PathVariable(value = "id") Long id);

}

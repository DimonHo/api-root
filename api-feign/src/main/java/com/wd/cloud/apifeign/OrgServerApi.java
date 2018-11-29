package com.wd.cloud.apifeign;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
@FeignClient(value = "org-server")
public interface OrgServerApi {

    @GetMapping("/orginfo/all")
    ResponseModel<List<JSONObject>> getAll();

    @GetMapping("/orginfo/{id}")
    ResponseModel getOrg(@PathVariable(value = "id") Long id);

}

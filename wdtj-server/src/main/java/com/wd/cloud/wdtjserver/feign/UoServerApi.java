package com.wd.cloud.wdtjserver.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/21
 * @Description:
 */
@FeignClient(value = "uo-server", fallback = UoServerApi.Fallback.class)
public interface UoServerApi {

    @GetMapping("/org/all")
    ResponseModel<List<JSONObject>> getAll();

    @GetMapping("/org/{id}")
    ResponseModel getOrg(@PathVariable(value = "id") Long id);

    @Component("uoServerApi")
    class Fallback implements UoServerApi {
        @Override
        public ResponseModel<List<JSONObject>> getAll() {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:org-server调用失败！");
        }

        @Override
        public ResponseModel getOrg(Long id) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:org-server调用失败！");
        }
    }
}

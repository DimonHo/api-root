package com.wd.cloud.wdtjserver.feign;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/21
 * @Description:
 */
@FeignClient(value = "org-server", fallback = OrgServerApi.Fallback.class)
public interface OrgServerApi extends com.wd.cloud.apifeign.OrgServerApi {

    @Component
    class Fallback implements OrgServerApi {
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

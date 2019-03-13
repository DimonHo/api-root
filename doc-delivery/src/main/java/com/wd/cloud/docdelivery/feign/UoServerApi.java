package com.wd.cloud.docdelivery.feign;


import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "uo-server", fallback = UoServerApi.Fallback.class)
public interface UoServerApi {

    @GetMapping("/org")
    ResponseModel<JSONObject> getOrg(@RequestParam(required = false) String orgName,
                                     @RequestParam(required = false) String flag,
                                     @RequestParam(required = false) String spisFlag,
                                     @RequestParam(required = false) String eduFlag,
                                     @RequestParam(required = false) String ip);

    @GetMapping("/user/info")
    ResponseModel<UserDTO> getUserInfo(@RequestParam(value = "username") String username);

    @Component("uoServerApi")
    class Fallback implements UoServerApi {

        @Override
        public ResponseModel<JSONObject> getOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:uo-server调用失败！");
        }

        @Override
        public ResponseModel<UserDTO> getUserInfo(String username) {
            return ResponseModel.fail(StatusEnum.FALL_BACK).setMessage("[fallback]:uo-server调用失败！");
        }
    }
}

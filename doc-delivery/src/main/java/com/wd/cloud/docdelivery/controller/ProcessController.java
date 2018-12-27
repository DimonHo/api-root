package com.wd.cloud.docdelivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
@RestController("/v2/backend")
public class ProcessController {

    @Autowired
    HttpServletRequest request;

//    /**
//     * 直接处理，上传文件
//     *
//     * @return
//     */
//    @ApiOperation(value = "直接处理，上传文件", tags = {"v2", "后台"})
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "helpRecordId", value = "求助数据id", dataType = "Long", paramType = "path"),
//            @ApiImplicitParam(name = "giverId", value = "应助者(处理人)id", dataType = "Long", paramType = "query"),
//            @ApiImplicitParam(name = "giverName", value = "应助者(处理人)username", dataType = "String", paramType = "query")
//    })
//    @PostMapping("/give/{helpRecordId}")
//    public ResponseModel give(@PathVariable Long helpRecordId,
//                              @NotNull MultipartFile file) {
//        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
//        Map<String, Object> userInfo = principal.getAttributes();
//        String userName = userInfo.get("username").toString();
//        Long userId = Long.parseLong(userInfo.get("id").toString());
//        return ResponseModel.ok();
//    }
}

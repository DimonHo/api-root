package com.wd.cloud.searchserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.searchserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

@RestController
public class TjController {
    @Autowired
    TjService tjService;

    /**
     * 获取下载量
     *
     * @param orgName
     * @param date
     * @return
     */
    @ApiOperation(value = "下载量统计", tags = {"统计"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "机构全称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "统计时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0:按秒统计，1:按分钟统计（默认），2：按小时统计，3：按天统计，4：按月统计，5：按年统计", dataType = "Integer", paramType = "query")
    })
    @GetMapping("/dc_count/name")
    public ResponseModel dcCountByOrgName(@RequestParam(required = false) String orgName,
                                          @RequestParam(required = false) String date,
                                          @RequestParam(required = false, defaultValue = "1") Integer type) {
        date = date != null ? date : DateUtil.now();
        Map<String, BigInteger> dcCountModels = tjService.tjDcCount(orgName, date, type);
        return ResponseModel.ok().setBody(dcCountModels);
    }


    /**
     * 获取检索量
     *
     * @param orgName
     * @param date
     * @return
     */
    @ApiOperation(value = "检索量统计", tags = {"统计"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "机构全称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "统计时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0:按秒统计，1:按分钟统计（默认），2：按小时统计，3：按天统计，4：按月统计，5：按年统计", dataType = "Integer", paramType = "query")
    })
    @GetMapping("/sc_count/name")
    public ResponseModel scCountByOrgName(@RequestParam(required = false) String orgName,
                                          @RequestParam(required = false) String date,
                                          @RequestParam(required = false, defaultValue = "1") Integer type) {
        date = date != null ? date : DateUtil.now();
        Map<String, BigInteger> scCountModels = tjService.tjScCount(orgName, date, type);
        return ResponseModel.ok().setBody(scCountModels);
    }
}

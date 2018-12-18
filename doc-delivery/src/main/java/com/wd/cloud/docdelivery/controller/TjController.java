package com.wd.cloud.docdelivery.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/18
 * @Description:
 */
@RestController
public class TjController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "文献传递量统计", tags = {"统计"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "机构全称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "统计时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0:按秒统计，1:按分钟统计（默认），2：按小时统计，3：按天统计，4：按月统计，5：按年统计", dataType = "Integer", paramType = "query")
    })
    @RequestMapping("/ddc_count/name")
    public ResponseModel ddcCountByOrgName(@RequestParam(required = false) String orgName,
                                  @RequestParam(required = false) String date,
                                  @RequestParam(required = false, defaultValue = "1") Integer type) {
        date = date != null ? date : DateUtil.now();
        Map<String, BigInteger> body = tjService.ddcCount(orgName, date, type);
        return ResponseModel.ok().setBody(body);
    }

}

package com.wd.cloud.docdelivery.controller;

import cn.hutool.http.HttpUtil;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.dto.MyTjDTO;
import com.wd.cloud.docdelivery.dto.TjDTO;
import com.wd.cloud.docdelivery.service.TjService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/18
 * @Description:
 */
@Api(value = "统计分析controller", tags = {"文献传递数据统计分析"})
@RestController
public class TjController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "用户统计")
    @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query")
    @GetMapping("/tj/user")
    public ResponseModel getUserHelpCountToDay(@RequestParam String email, HttpServletRequest request) {

        MyTjDTO myTotalModel = tjService.tjUser(email, HttpUtil.getClientIP(request));
        //未完待续
        return ResponseModel.ok().setBody(myTotalModel);
    }

    @ApiOperation(value = "文献传递量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "机构全称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "统计时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0:按秒统计，1:按分钟统计（默认），2：按小时统计，3：按天统计，4：按月统计，5：按年统计", dataType = "Integer", paramType = "query")
    })
    @GetMapping("/ddc_count/name")
    public ResponseModel ddcCountByOrgName(@RequestParam(required = false) String orgName,
                                           @RequestParam(required = false) String date,
                                           @RequestParam(required = false, defaultValue = "1") Integer type) {
        date = date != null ? date : DateUtil.now();
        Map<String, BigInteger> body = tjService.ddcCount(orgName, date, type);
        return ResponseModel.ok().setBody(body);
    }


    @ApiOperation(value = "获取平台总求助量、成功量、成功率、今日求助量")
    @GetMapping("/tj/total")
    public ResponseModel<TjDTO> getHeadTotalFor() {
        TjDTO body = new TjDTO();
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        // 求助总数量
        long totalForHelp = tjService.totalForHelp();
        body.setTotalForHlep(totalForHelp);
        //求助成功的数量
        long successTotal = tjService.successTotal();
        body.setSuccessTotal(successTotal);
        //求助成功率
        String successRate = numberFormat.format((float) successTotal / (float) totalForHelp);
        body.setSuccessRate(successRate);
        //今天求助数量
        body.setTodayTotalForHelp(tjService.todayTotalForHelp());

        return ResponseModel.ok().setBody(body);
    }

}

package com.wd.cloud.docdelivery.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.annotation.ValidateLogin;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.pojo.dto.MyTjDTO;
import com.wd.cloud.docdelivery.pojo.dto.TjDTO;
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

    @Autowired
    HttpServletRequest request;


    @ApiOperation(value = "邮箱统计")
    @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query")
    @GetMapping("/tj")
    public ResponseModel getEmailHelpCountToDay(@RequestParam String email) {
        try {
            MyTjDTO myTotalModel = tjService.tjEmail(email, HttpUtil.getClientIP(request));
            return ResponseModel.ok().setBody(myTotalModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @ApiOperation(value = "我的统计")
    @ValidateLogin
    @GetMapping("/tj/my")
    public ResponseModel getUserHelpCountToDay() {
        try {
            JSONObject loginUser = (JSONObject) request.getSession().getAttribute(SessionConstant.LOGIN_USER);
            String username = loginUser != null ? loginUser.getStr("username") : null;
            MyTjDTO myTotalModel = tjService.tjUser(username);
            return ResponseModel.ok().setBody(myTotalModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
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
    public ResponseModel getHeadTotalFor() {
        TjDTO body = tjService.tjForHelp();
        return ResponseModel.ok().setBody(body);
    }

    @ApiOperation(value = "获取平均响应时间")
    @ApiImplicitParam(name = "startDate", value = "起始统计时间", dataType = "String", paramType = "query")
    @GetMapping("/tj/avg-time")
    public ResponseModel avgTime(@RequestParam(required = false, defaultValue = "2019-01-01 00:00:00") String startDate) {
        long avgResponseTime = tjService.avgResponseTime(startDate);
        long avgSuccessResponseTime = tjService.avgSuccessResponseTime(startDate);
        JSONObject avgResponseJson = new JSONObject();
        avgResponseJson.put("avgResponseTime", avgResponseTime);
        avgResponseJson.put("avgSuccessResponseTime", avgSuccessResponseTime);
        return ResponseModel.ok().setBody(avgResponseJson);
    }

}

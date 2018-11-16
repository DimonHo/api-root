package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Descriptiwon:
 */
@RestController
@RequestMapping("/view")
public class ViewController {
    @Autowired
    TjService tjService;

    @ApiOperation(value = "按年展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/year/{orgId}")
    public ResponseModel getYear(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate) {
        List<TjViewData> tjViewData = new ArrayList<>();
        List<Map<String, Object>> listYear = tjService.findByTjDateAndOrgIdYear(beginDate, endDate, orgId);
        return ResponseModel.ok().setBody(listYear);
    }

    @ApiOperation(value = "按月展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/month/{orgId}")
    public ResponseModel getMonth(@PathVariable Long orgId,
                                  @RequestParam Date beginDate,
                                  @RequestParam Date endDate) {
        List<Map<String, Object>> listMonth = tjService.findByTjDateAndOrgIdMonth(beginDate, endDate, orgId);
        return ResponseModel.ok().setBody(listMonth);

    }

    @ApiOperation(value = "按天展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/day/{orgId}")
    public ResponseModel getDay(@PathVariable Long orgId,
                                @RequestParam Date beginDate,
                                @RequestParam Date endDate) {
        List<Map<String, Object>> listDay = tjService.findByTjDateAndOrgIdDay(beginDate, endDate, orgId);
        return ResponseModel.ok().setBody(listDay);

    }

    @ApiOperation(value = "按小时展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/hour/{orgId}")
    public ResponseModel getHour(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate) {
        List<Map<String, Object>> listHour = tjService.findByTjDateAndOrgIdTime(beginDate, endDate, orgId);
        return ResponseModel.ok().setBody(listHour);
    }
}

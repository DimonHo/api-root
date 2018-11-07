package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Descriptiwon:
 */
@RestController
public class ViewController {

    @ApiOperation(value = "按年展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/view/year/{orgId}")
    public ResponseModel getYear(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate){
        return ResponseModel.ok();
    }

    @ApiOperation(value = "按月展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/view/month/{orgId}")
    public ResponseModel getMonth(@PathVariable Long orgId,
                                  @RequestParam Date beginDate,
                                  @RequestParam Date endDate){
        return ResponseModel.ok();
    }

    @ApiOperation(value = "按天展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/view/day/{orgId}")
    public ResponseModel getDay(@PathVariable Long orgId,
                                @RequestParam Date beginDate,
                                @RequestParam Date endDate){

        return ResponseModel.ok();
    }

    @ApiOperation(value = "按小时展示数据", tags = {"前台数据"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @GetMapping("/view/hour/{orgId}")
    public ResponseModel getHour(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate){
        return ResponseModel.ok();
    }
}

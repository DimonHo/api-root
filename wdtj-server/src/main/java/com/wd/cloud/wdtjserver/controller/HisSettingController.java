package com.wd.cloud.wdtjserver.controller;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class HisSettingController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "Date", paramType = "query")
    })
    @PostMapping("/setting/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody List<HisQuotaModel> hisQuotaModels
                             ) {
        TjHisSetting tjHisSetting=new TjHisSetting();
        for(HisQuotaModel hisQuotaModel:hisQuotaModels){
            tjHisSetting.setPvCount(hisQuotaModel.getPvCount());
            tjHisSetting.setOrgId(orgId);
            tjHisSetting.setScCount(hisQuotaModel.getScCount());
            tjHisSetting.setDcCount(hisQuotaModel.getDcCount());
            tjHisSetting.setDdcCount(hisQuotaModel.getDdcCount());
            tjHisSetting.setAvgTime(Time.valueOf(hisQuotaModel.getAvgTime()));
            tjHisSetting.setBeginTime(hisQuotaModel.getBeginTime());
            tjHisSetting.setEndTime(hisQuotaModel.getEndTime());
            TjHisSetting hisSetting = tjService.save(tjHisSetting);
        }

        return ResponseModel.ok().setBody(tjHisSetting);
    }

    @ApiOperation(value = "按月设置历史基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "Date", paramType = "path")
    })
    @PostMapping("/setting/his/{orgId}/{month}")
    public ResponseModel addFromMonth(@PathVariable Long orgId,
                                      @RequestBody QuotaModel quotaModel,
                                      @PathVariable Date month) {
        return ResponseModel.ok();
    }
}

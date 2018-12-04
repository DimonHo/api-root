package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.service.QuotaService;
import com.wd.cloud.wdtjserver.utils.DateUtil;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
@RestController
public class QuotaController {

    @Autowired
    QuotaService quotaService;

    @ApiOperation(value = "设置日基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "createUser", value = "创建人名称", dataType = "String", paramType = "query")
    })
    @PostMapping("/quota/{orgId}")
    public ResponseModel addQuota(@PathVariable Long orgId,
                                  @RequestParam String createUser,
                                  @RequestBody @Valid QuotaModel quotaModel) {
        TjQuota tjQuota = ModelUtil.build(quotaModel);
        tjQuota.setOrgId(orgId).setCreateUser(createUser);
        TjQuota body = quotaService.save(tjQuota);
        if (body == null) {
            return ResponseModel.fail().setMessage("数据保存失败");
        }
        return ResponseModel.ok().setBody(body);
    }

    @ApiOperation(value = "获取所有机构日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "history", value = "是否生效", dataType = "Boolean", paramType = "query")
    @GetMapping("/quota/all")
    public ResponseModel findOrgQuotaAll(@RequestParam(required = false) Boolean history,
                                         @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = quotaService.findAll(history, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }

    @ApiOperation(value = "获取机构正在使用的日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}")
    public ResponseModel findOrgQuota(@PathVariable Long orgId,
                                      @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        TjQuota tjQuota = quotaService.findOrgQuota(orgId);
        return ResponseModel.ok().setBody(tjQuota);
    }

    @ApiOperation(value = "获取机构历史日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}/his")
    public ResponseModel findOrgQuotaHis(@PathVariable Long orgId,
                                         @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = quotaService.findOrgQuota(orgId, true, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }

    @ApiOperation(value = "机构所有日基数设置记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}/all")
    public ResponseModel findQuota(@PathVariable Long orgId,
                                   @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = quotaService.findOrgQuota(orgId, null, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }


    @ApiOperation(value = "机构名称或操作人模糊查询", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "机构名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "history", value = "是否是历史记录", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/quota/find")
    public ResponseModel<Page> find(@RequestParam(required = false) String query,
                                    @RequestParam(required = false) Boolean history,
                                    @PageableDefault(sort = {"orgName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjQuota> orgPage = quotaService.likeQuery(query, history, pageable);
        return ResponseModel.ok().setBody(orgPage);
    }


    @ApiOperation(value = "手动根据日基数生成详细数据", tags = {"后台管理"})
    @ApiImplicitParam(name = "day", value = "要生成的日期", dataType = "String", paramType = "query")
    @GetMapping("/quota/run")
    public ResponseModel runTodayQuota(@RequestParam String day) {
        quotaService.runTask(DateUtil.parse(day));
        return ResponseModel.ok();
    }
}

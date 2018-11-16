package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.QuotaModel;
import com.wd.cloud.wdtjserver.service.TjService;
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

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class SettingController {

    @Autowired
    TjService tjService;

    @ApiOperation(value = "设置机构参数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "showPv", value = "是否显示访问量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showSc", value = "是否显示搜索量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDc", value = "是否显示下载量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDdc", value = "是否显示文献传递量", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "showAvgTime", value = "是否显示平均访问时长", dataType = "boolean", paramType = "query")
    })
    @PostMapping("/org/{orgId}")
    public ResponseModel<TjOrg> add(
            @PathVariable Long orgId,
            @RequestParam(required = false, defaultValue = "false") boolean showPv,
            @RequestParam(required = false, defaultValue = "false") boolean showSc,
            @RequestParam(required = false, defaultValue = "false") boolean showDc,
            @RequestParam(required = false, defaultValue = "false") boolean showDdc,
            @RequestParam(required = false, defaultValue = "false") boolean showAvgTime) {
        TjOrg tjOrg = new TjOrg();
        tjOrg.setOrgId(orgId).setShowPv(showPv).setShowSc(showSc).setShowDc(showDc).setShowDdc(showDdc).setShowAvgTime(showAvgTime);
        tjOrg = tjService.save(tjOrg);
        if (tjOrg != null) {
            return ResponseModel.ok().setBody(tjOrg);
        }
        return ResponseModel.fail(StatusEnum.NOT_FOUND);

    }

    /**
     * 根据机构名称查询
     *
     * @param orgName
     * @return
     */
    @ApiOperation(value = "根据机构名称模糊查询", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "机构名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "history", value = "是否是历史记录", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/org/find")
    public ResponseModel<Page> find(@RequestParam(required = false) String orgName,
                                    @RequestParam(required = false) Boolean history,
                                    @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjOrg> orgList = tjService.likeOrgName(orgName, history, pageable);
        return ResponseModel.ok().setBody(orgList);
    }

    /**
     * 获取所有已生效的机构列表
     *
     * @return
     */
    @ApiOperation(value = "获取所有已生效的机构列表", tags = {"后台设置"})
    @GetMapping("/org/all")
    public ResponseModel<Page> all(@PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjOrg> orgEnableList = tjService.getEnabledFromAll(pageable);
        return ResponseModel.ok().setBody(orgEnableList);
    }

    /**
     * 获取所有历史的机构列表
     *
     * @return
     */
    @ApiOperation(value = "获取所有历史的机构列表", tags = {"后台设置"})
    @GetMapping("/org/his")
    public ResponseModel<Page> history(@PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjOrg> orgHisList = tjService.getHistoryFromAll(pageable);
        return ResponseModel.ok().setBody(orgHisList);
    }

    /**
     * 根据指标过滤
     *
     * @param showPv
     * @param showSc
     * @param showDc
     * @param showDdc
     * @param showAvgTime
     * @return
     */
    @ApiOperation(value = "根据指标过滤机构设置列表", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "showPv", value = "是否有访问量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showSc", value = "是否有搜索量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDc", value = "是否有下载量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showDdc", value = "是否有文献传递量指标", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "showAvgTime", value = "是否有平均访问时长指标", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/org/filter")
    public ResponseModel<TjOrg> find(@RequestParam(required = false) Boolean showPv,
                                     @RequestParam(required = false) Boolean showSc,
                                     @RequestParam(required = false) Boolean showDc,
                                     @RequestParam(required = false) Boolean showDdc,
                                     @RequestParam(required = false) Boolean showAvgTime,
                                     @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseModel.ok().setBody(tjService.filterByQuota(showPv, showSc, showDc, showDdc, showAvgTime, pageable));
    }


    @ApiOperation(value = "设置日基数", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @PostMapping("/quota/{orgId}")
    public ResponseModel addQuota(@PathVariable Long orgId,
                                  @RequestBody QuotaModel quotaModel) {
        TjQuota tjQuota = ModelUtil.build(quotaModel);
        tjQuota.setOrgId(orgId);
        return ResponseModel.ok().setBody(tjService.save(tjQuota));
    }

    @ApiOperation(value = "获取所有机构日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "history", value = "是否生效", dataType = "Boolean", paramType = "query")
    @GetMapping("/quota/all")
    public ResponseModel findOrgQuotaAll(@RequestParam(required = false) Boolean history,
                                         @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = tjService.findAll(history, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }

    @ApiOperation(value = "获取机构正在使用的日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}")
    public ResponseModel findOrgQuota(@PathVariable Long orgId,
                                      @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        TjQuota tjQuota = tjService.findOrgQuota(orgId);
        return ResponseModel.ok().setBody(tjQuota);
    }

    @ApiOperation(value = "获取机构历史日基数设置", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}/his")
    public ResponseModel findOrgQuotaHis(@PathVariable Long orgId,
                                         @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = tjService.findOrgQuota(orgId, true, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }

    @ApiOperation(value = "机构所有日基数设置记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/quota/{orgId}/all")
    public ResponseModel findQuota(@PathVariable Long orgId,
                                   @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjQuota> tjQuotas = tjService.findOrgQuota(orgId, null, pageable);
        return ResponseModel.ok().setBody(tjQuotas);
    }


    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @PostMapping("/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody List<HisQuotaModel> hisQuotaModels) {
        // 检查时间区间是否允许修改
        Map<String, DateIntervalModel> overlapsMap = tjService.checkInterval(orgId, hisQuotaModels);
        if (overlapsMap.size() > 0) {
            return ResponseModel.fail().setBody(overlapsMap);
        }
        List<TjHisQuota> tjHisQuotas = tjService.save(orgId, hisQuotaModels);
        return ResponseModel.ok().setBody(tjHisQuotas);
    }

    @ApiOperation(value = "生成历史详细记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "hisId", value = "历史记录Id", dataType = "Long", paramType = "path")
    @PatchMapping("/his/build/{hisId}")
    public ResponseModel build(@PathVariable Long hisId) {
        TjHisQuota tjHisQuota = tjService.get(hisId);
        if (tjHisQuota == null) {
            return ResponseModel.fail(StatusEnum.NOT_FOUND);
        } else if (tjHisQuota.isLocked()) {
            return ResponseModel.fail().setMessage("该记录已生成且已锁定");
        }
        tjService.buildTjHisData(tjHisQuota);
        return ResponseModel.ok();
    }


}

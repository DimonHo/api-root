package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.service.HisQuotaService;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
@RestController
public class HisQuotaController {

    @Autowired
    HisQuotaService hisQuotaService;

    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @PostMapping("/his/{orgId}")
    public ResponseModel add(@PathVariable Long orgId,
                             @RequestBody List<HisQuotaModel> hisQuotaModels) {
        // 检查时间区间是否允许修改
        Map<String, DateIntervalModel> overlapsMap = hisQuotaService.checkInterval(orgId, hisQuotaModels);
        if (overlapsMap.size() > 0) {
            return ResponseModel.fail().setBody(overlapsMap);
        }
        List<TjHisQuota> tjHisQuotas = new ArrayList<>();
        hisQuotaModels.forEach(hisQuotaModel -> {
            tjHisQuotas.add(ModelUtil.build(hisQuotaModel).setOrgId(orgId));
        });
        return ResponseModel.ok().setBody(hisQuotaService.save(tjHisQuotas));
    }

    @ApiOperation(value = "机构历史指标记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgId", value = "机构Id", dataType = "Long", paramType = "path")
    @GetMapping("/his/{orgId}")
    public ResponseModel findHisByOrg(@PathVariable Long orgId,
                                      @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjHisQuota> tjHisQuotas = hisQuotaService.getHisQuotaByOrg(orgId, pageable);
        return ResponseModel.ok().setBody(tjHisQuotas);
    }


    @ApiOperation(value = "所有历史指标记录", tags = {"后台设置"})
    @GetMapping("/his/all")
    public ResponseModel findHisAll(@PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseModel.ok().setBody(hisQuotaService.getAllHisQuota(pageable));
    }

    @ApiOperation(value = "生成历史详细记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "hisId", value = "历史记录Id", dataType = "Long", paramType = "path")
    @PatchMapping("/his/build/{hisId}")
    public ResponseModel build(@PathVariable Long hisId) {
        TjHisQuota tjHisQuota = hisQuotaService.getHisQuota(hisId);
        if (tjHisQuota == null) {
            return ResponseModel.fail(StatusEnum.NOT_FOUND);
        } else if (tjHisQuota.isLocked()) {
            return ResponseModel.fail().setMessage("该记录已生成且已锁定");
        }
        hisQuotaService.buildTjHisData(tjHisQuota);
        return ResponseModel.ok();
    }
}

package com.wd.cloud.wdtjserver.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.service.HisQuotaService;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
@Validated
@RestController
public class HisQuotaController {

    private static final Log log = LogFactory.get();

    @Autowired
    HisQuotaService hisQuotaService;

    @ApiOperation(value = "设置历史基数", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgFlag", value = "机构Id", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "createUser", value = "创建人名称", dataType = "String", paramType = "query")
    })
    @PostMapping("/his/{orgFlag}")
    public ResponseModel add(@PathVariable String orgFlag,
                             @RequestParam String createUser,
                             @Valid @RequestBody List<HisQuotaModel> hisQuotaModels) {
        // 检查时间区间是否允许修改
        Map<String, DateIntervalModel> overlapsMap = hisQuotaService.checkInterval(orgFlag, hisQuotaModels);
        if (overlapsMap.size() > 0) {
            return ResponseModel.fail().setBody(overlapsMap);
        }
        List<TjHisQuota> tjHisQuotas = new ArrayList<>();
        hisQuotaModels.forEach(hisQuotaModel -> {
            TjHisQuota tjHisQuota = ModelUtil.build(hisQuotaModel);
            if (tjHisQuota != null) {
                tjHisQuotas.add(ModelUtil.build(hisQuotaModel).setOrgFlag(orgFlag).setCreateUser(createUser));
            }
        });
        List<TjHisQuota> body = hisQuotaService.save(tjHisQuotas);
        log.info("开始生成历史数据");
        // 数据保存成功，自动生成历史数据
        body.forEach(tjHisQuota -> {
            //更新记录状态为记录生成中
            hisQuotaService.buildingState(tjHisQuota, createUser);
            hisQuotaService.buildExecute(tjHisQuota);
        });
        log.info("生成历史数据完成");
        return ResponseModel.ok().setBody(body);
    }

    @ApiOperation(value = "机构历史指标记录", tags = {"后台设置"})
    @ApiImplicitParam(name = "orgFlag", value = "机构Id", dataType = "String", paramType = "path")
    @GetMapping("/his/{orgFlag}")
    public ResponseModel findHisByOrg(@PathVariable String orgFlag,
                                      @PageableDefault(sort = {"gmtModified"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TjHisQuota> tjHisQuotas = hisQuotaService.getHisQuotaByOrg(orgFlag, pageable);
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
    public ResponseModel build(@PathVariable Long hisId,
                               @RequestParam String buildUser) {
        TjHisQuota tjHisQuota = hisQuotaService.getHisQuota(hisId);
        if (tjHisQuota == null) {
            return ResponseModel.fail(StatusEnum.NOT_FOUND);
        } else if (tjHisQuota.isLocked()) {
            return ResponseModel.fail().setMessage("记录已生成且已锁定");
        } else if (tjHisQuota.getBuildState() == 2) {
            return ResponseModel.fail().setMessage("记录正在生成中。。。请稍后再查看结果");
        }
        //更新记录状态为记录生成中
        hisQuotaService.buildingState(tjHisQuota, buildUser);
        //执行生成记录
        hisQuotaService.buildExecute(tjHisQuota);
        return ResponseModel.ok();
    }


    @ApiOperation(value = "批量生成历史详细记录", tags = {"后台设置"})
    @PatchMapping("/his/buildAll")
    public ResponseModel buildAll(@RequestParam String buildUser) {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<TjHisQuota> tjHisQuotaPage = hisQuotaService.getAllHisQuota(pageable);
        tjHisQuotaPage.forEach(tjHisQuota -> {
            if (tjHisQuota.isLocked()) {
                log.warn("记录已生成且已锁定");
            } else if (tjHisQuota.getBuildState() == 2) {
                log.info("记录正在生成中。。。请稍后再查看结果");
            }
            //更新记录状态为记录生成中
            hisQuotaService.buildingState(tjHisQuota, buildUser);
            //执行生成记录
            hisQuotaService.buildExecute(tjHisQuota);
        });
        return ResponseModel.ok();
    }


    @ApiOperation(value = "机构名称或操作人模糊查询", tags = {"后台设置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "机构名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "history", value = "是否是历史记录", dataType = "Boolean", paramType = "query")
    })
    @GetMapping("/his/find")
    public ResponseModel<Page> find(@RequestParam(required = false) String query,
                                    @RequestParam(required = false) Boolean history,
                                    @PageableDefault(sort = {"orgName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TjHisQuota> orgPage = hisQuotaService.likeQuery(query, history, pageable);
        return ResponseModel.ok().setBody(orgPage);
    }
}

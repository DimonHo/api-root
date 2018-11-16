package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.service.TjService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @ApiOperation(value = "获取机构统计数据", tags = {"数据展示"})
    @GetMapping("/{orgId}")
    public ResponseModel getData(@PathVariable Long orgId,
                                 @RequestParam Date beginTime,
                                 @RequestParam Date endTime) {


        return ResponseModel.ok();
    }
}

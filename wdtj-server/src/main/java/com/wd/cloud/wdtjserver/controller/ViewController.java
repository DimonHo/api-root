package com.wd.cloud.wdtjserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@RestController
public class ViewController {

    @GetMapping("/view/year/{orgId}")
    public ResponseModel getYear(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate){
        return ResponseModel.ok();
    }

    @GetMapping("/view/month/{orgId}")
    public ResponseModel getMonth(@PathVariable Long orgId,
                                  @RequestParam Date beginDate,
                                  @RequestParam Date endDate){
        return ResponseModel.ok();
    }

    @GetMapping("/view/day/{orgId}")
    public ResponseModel getDay(@PathVariable Long orgId,
                                @RequestParam Date beginDate,
                                @RequestParam Date endDate){
        return ResponseModel.ok();
    }

    @GetMapping("/view/hour/{orgId}")
    public ResponseModel getHour(@PathVariable Long orgId,
                                 @RequestParam Date beginDate,
                                 @RequestParam Date endDate){
        return ResponseModel.ok();
    }
}

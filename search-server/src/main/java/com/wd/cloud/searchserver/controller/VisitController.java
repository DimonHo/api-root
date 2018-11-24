package com.wd.cloud.searchserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.searchserver.service.FlowAnalysisServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
@RestController
public class VisitController {

    @Autowired
    private FlowAnalysisServiceI flowAnalysisService;

    @GetMapping("/indexVisit")
    public ResponseModel indexVisit(@RequestParam Long orgId,
                                    @RequestParam String tjDate){
        List<Map<String, Object>> list =flowAnalysisService.visite(orgId, tjDate);
        return ResponseModel.ok().setBody(list);
    }
}

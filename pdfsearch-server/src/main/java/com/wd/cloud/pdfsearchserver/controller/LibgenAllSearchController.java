package com.wd.cloud.pdfsearchserver.controller;

import com.alibaba.fastjson.JSON;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.model.LiteratureModel;
import com.wd.cloud.pdfsearchserver.service.LibgenAllSearchServiceI;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LibgenAllSearchController {
    @Autowired
    private LibgenAllSearchServiceI libgenSearchService;

    @ApiOperation(value = "根据标题或doi检索libgen_all获取数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "val", value = "文章标题或doi", dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "/searchPaper")
    public ResponseModel<List<JSON>> getRowKey(@RequestParam String val){
        return libgenSearchService.getResult(val);
    }
}

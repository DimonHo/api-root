package com.wd.cloud.pdfsearchserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.model.LiteratureModel;
import com.wd.cloud.pdfsearchserver.service.pdfSearchServiceI;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class pdfSearchController {
    @Autowired
    private pdfSearchServiceI pdfSearchService;

    @ApiOperation(value = "全文检索获取文件id接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docTitle", value = "文章标题,必填不能为空", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "author", value = "作者，多个以分号隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "journal", value = "期刊标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "volume", value = "卷", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "issue", value = "期", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "issn", value = "issn", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "doi", value = "doi", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/searchpdf")
    public ResponseModel<String> getRowKey(@RequestBody LiteratureModel literatureModel){
        String title = literatureModel.getDocTitle();
        if(title==null || "".equals(title)){
            return ResponseModel.fail().setMessage("标题不能为空");
        }
        return pdfSearchService.getRowKey(literatureModel);
    }

    @ApiOperation(value = "获取全文接口")
    @ApiImplicitParams(@ApiImplicitParam(name = "rowKey", value = "文件id", dataType = "String", paramType = "path"))
    @GetMapping(value = "/search/{rowKey}")
    public ResponseModel<byte[]> getpdf(@PathVariable String rowKey){
        return pdfSearchService.getpdf(rowKey);
    }
}

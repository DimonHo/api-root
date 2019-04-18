package com.wd.cloud.crsserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.crsserver.service.OafindService;
import com.wd.cloud.crsserver.service.SearchServcie;
import com.weidu.commons.search.SearchCondition;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 10:34
 * @Description:
 */
@RestController
public class OafindController {

    @Autowired
    OafindService oafindService;

    @Autowired
    SearchServcie searchServcie;


    @GetMapping("/{index}/{id}")
    public ResponseModel getId(@PathVariable String index,@PathVariable String id){
       return  ResponseModel.ok().setBody(searchServcie.getById(index,id));
    }

    @PostMapping("/{index}")
    public ResponseModel getId(@PathVariable String index,@RequestBody String[] ids){
        return  ResponseModel.ok().setBody(searchServcie.findByIds(index,ids));
    }

    @PutMapping("/{index}/{type}/{id}")
    public ResponseModel updateById(@PathVariable String index, @PathVariable String type, @PathVariable String id, @RequestBody Map<String,Object> source){
        searchServcie.updateById(index,type,id,source);
        return ResponseModel.ok();
    }

    @PostMapping("/index/{index}/{type}")
    public ResponseModel index(@PathVariable String index, @PathVariable String type, @RequestBody XContentBuilder source){
        searchServcie.index(index,type,source);
        return ResponseModel.ok();
    }

    /**
     * 普通查询
     *
     * @return
     */
    @GetMapping("/search")
    public ResponseModel search(@RequestParam(required = false) String queryStr,
                                @RequestParam(required = false) Integer startYear,
                                @RequestParam(required = false) Integer endYear,
                                @RequestParam(required = false) List<Integer> code,
                                @RequestParam(required = false) List<String> source,
                                @RequestParam(required = false) List<Integer> year,
                                @RequestParam(required = false) List<String> journal,
                                @RequestParam(required = false) List<Integer> language,
                                @RequestParam(required = false) String aggFiled,
                                @PageableDefault Pageable pageable) {
        AggregatedPage oafinds = oafindService.baseSearch(queryStr, startYear, endYear, code, source, year, journal, language, aggFiled, pageable);
        return ResponseModel.ok().setBody(oafinds);
    }


    /**
     * 精确查询
     *
     * @return
     */
    @PostMapping("/query")
    public ResponseModel exactSearch(@RequestBody List<SearchField> searchField,
                                     @PageableDefault Pageable pageable) {
        return ResponseModel.ok().setBody(oafindService.exactSearch(searchField, pageable));
    }

    @PostMapping("/search")
    public ResponseModel search(@RequestBody SearchCondition searchCondition) {
        return ResponseModel.ok().setBody(searchServcie.search(searchCondition));
    }

}

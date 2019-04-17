package com.wd.cloud.bse.controller;

import com.wd.cloud.bse.entity.school.Scholar;
import com.wd.cloud.bse.entity.xk.IndexLog;
import com.wd.cloud.bse.entity.xk.Issue;
import com.wd.cloud.bse.service.BseService;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.service.IndexLogService;
import com.wd.cloud.bse.service.ScholarService;
import com.wd.cloud.bse.util.ParamsAnalyze;
import com.wd.cloud.bse.vo.*;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "学科bse接口", tags = {""})
@RestController
public class IndexController {

    @Autowired
    BseService bseService;

    @Autowired
    ScholarService scholarService;

    @Autowired
    CacheService cacheService;

    @Autowired
    IndexLogService indexLogService;

    @ApiOperation(value = "专家学者发表论文列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "查询学者id", dataType = "String", paramType = "query")
    })
    @RequestMapping("/bse")
    public ResponseModel bse(HttpServletRequest request) {
        try {
            SearchCondition condition = new SearchCondition();
            String id = request.getParameter("id");
            Scholar scholar = scholarService.findById(Integer.parseInt(id));
            condition.addQueryCondition(new QueryCondition("authorQuery", "author", scholar.getName()));
            String schoolSmail = scholar.getSchoolSmail();
            if (StringUtils.isNotBlank(schoolSmail)) {
                String[] schoolSmails = schoolSmail.split(";");
                if (schoolSmails.length == 1) {
                    condition.addQueryCondition(new QueryCondition("orgQuery", "org", schoolSmails[0]));
                } else {
                    condition.addQueryCondition(new QueryCondition("orgQuery", "org", schoolSmails[0], 2));
                    condition.addQueryCondition(new QueryCondition("orgQuery", "org", schoolSmails[1], 2));
                }
            }
            condition.addSort("documents.year", "documents", 2);
            condition.setSize(5000);
            condition.setIsFacets(1);
            condition.setIndexName("res");
            condition.setTypes(new String[]{"paper"});
            List<String> result = bseService.searchScholar(condition, schoolSmail);
            return ResponseModel.ok().setBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }


    @RequestMapping("/bseByAuthor")
    public ResponseModel bseByAuthor(HttpServletRequest request) {
        String author = request.getParameter("author");
        String scid = request.getParameter("scid");
        SearchCondition condition = new SearchCondition();

        condition.addQueryCondition(new QueryCondition("authorQuery", "author", author));
        condition.addQueryCondition(new QueryCondition("commTermsQuery", "scids", scid));
        condition.setSize(5000);
        condition.setIndexName("res");
        condition.setTypes(new String[]{"paper"});
        List<String> result = bseService.searchScholar(condition, "");
        return ResponseModel.ok().setBody(result);
    }


    @ApiOperation(value = "学科数据列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/search")
    public ResponseModel search(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            if (StringUtils.isEmpty(reqParams)) {
                reqParams = "<params><types>[1,2,3]</types><is_facets>1</is_facets><page>0</page><size>10</size></params>";
            }
            indexLogService.save(new IndexLog("search", reqParams));
//    		reqParams = "<params><queries><field><name>relationSubject</name><value>Marine &amp; Freshwater Biology</value><logic>2</logic></field><field><name>relationSubject</name><value>Toxicology</value><logic>2</logic></field></queries><filters><field><name>docType</name><value>1</value></field><field><name>shoulu</name><value>SCI-E</value></field></filters><types>[\"1\"]</types><school>0</school><page>1</page><size>10</size></params>";
//    		reqParams = "<params><filters><field><name>scids</name><value>670</value></field></filters><types>[3]</types><page>1</page><size>20</size></params>";
//    		reqParams = "<params><filters><field><name>shoulu</name><value>EI</value></field><field><name>authorList</name><value>Chen, Xuehua</value></field></filters><types>[1,3]</types><sort>[3]</sort><page>1</page><size>20</size></params>";

            QueryParam params = ParamsAnalyze.parse(reqParams);
            SearchCondition condition = params.converToSearchCondition();
            condition.setIndexName("res_xk");
            SearchPager pager = bseService.query(condition);
            return ResponseModel.ok().setBody(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "新发表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/searchNew")
    public ResponseModel searchNew(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            if (StringUtils.isEmpty(reqParams)) {
                return ResponseModel.fail();
            }
            indexLogService.save(new IndexLog("searchNew", reqParams));
            QueryParam params = ParamsAnalyze.parse(reqParams);
            SearchCondition condition = params.converToSearchCondition();
            condition.setIndexName("res_xk");
            SearchPager pager = bseService.searchNew(condition);
            return ResponseModel.ok().setBody(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "esi热点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/searchEsiHot")
    public ResponseModel searchEsiHot(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            if (StringUtils.isEmpty(reqParams)) {
                return ResponseModel.fail();
            }
            indexLogService.save(new IndexLog("searchEsiHot", reqParams));
            QueryParam params = ParamsAnalyze.parse(reqParams);
            SearchCondition condition = params.converToSearchCondition();
            condition.setIndexName("res_xk");
            SearchPager pager = bseService.searchEsiHot(condition);
            return ResponseModel.ok().setBody(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "esi高被引")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/searchEsiTop")
    public ResponseModel searchEsiTop(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            if (StringUtils.isEmpty(reqParams)) {
                return ResponseModel.fail();
            }
            indexLogService.save(new IndexLog("searchEsiTop", reqParams));
            QueryParam params = ParamsAnalyze.parse(reqParams);
            SearchCondition condition = params.converToSearchCondition();
            condition.setIndexName("res_xk");
            SearchPager pager = bseService.searchEsiTop(condition);
            return ResponseModel.ok().setBody(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "详情查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/getDoc")
    public ResponseModel getDoc(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            if (StringUtils.isEmpty(reqParams)) {
                reqParams = "<params><types>[1]</types><page>1</page><size>0</size></params>";
            }
            indexLogService.save(new IndexLog("getDoc", reqParams));
//    		reqParams = "<params><ids>[\"483d8df811998e3f79e6310009\",\"a2d5d5eb12019a8b61fd7c601531\",\"d350c90b12019045cba29c592732\"]</ids></params>";
            QueryParam params = ParamsAnalyze.parse(reqParams);
            String[] idArr = params.getIds(null);
            SearchPager pager = bseService.getDocByIds(idArr, "res_xk");
            return ResponseModel.ok().setBody(pager.getRows());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "年份范围查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/getRangYear")
    public ResponseModel getRangYear(HttpServletRequest request) {
        try {
            String reqParams = request.getParameter("params");
            indexLogService.save(new IndexLog("getRangYear", reqParams));
            QueryParam params = ParamsAnalyze.parse(reqParams);
            Map<String, Object> map = new HashMap<>();

            for (DocType docType : DocType.values()) {
                SearchCondition condition = params.converToSearchCondition();
                List<FacetField> facets = new ArrayList<>();
                facets.add(new FacetField("yearRange", "year", 100, true));
//        		SearchCondition condition = new SearchCondition();
                condition.setFacetFields(facets);
                condition.setIndexName("res_xk");

                condition.addFilterCondition(new QueryCondition("docType", docType.getKey() + ""));
                condition.setTypes(new String[]{docType.getValue()});
                condition.setSize(0);
                SearchPager pager = bseService.query(condition);
                map.put(docType.getKey() + "", pager.getYearRange());
            }
            return ResponseModel.ok().setBody(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

    @ApiOperation(value = "更新时间")
    @RequestMapping("/getUpdateTime")
    public ResponseModel getUpdateTime() {
        Issue issue = cacheService.getIssue();
        return ResponseModel.ok().setBody(issue);
    }

    @ApiOperation(value = "类型数量统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping("/getDocTypeTotle")
    public ResponseModel getDocTypeTotle(HttpServletRequest request) {
        try {
            Map<Integer, Object> map = new HashMap<>();
            String reqParams = request.getParameter("params");
            indexLogService.save(new IndexLog("getDocTypeTotle", reqParams));
            QueryParam params = ParamsAnalyze.parse(reqParams);
            SearchCondition condition = params.converToSearchCondition();
            for (DocType docType : DocType.values()) {
//    			String reqParams = "<params><filters><field><name>docType</name><value>"+doctype.getKey()+"</value></field></filters><types>["+doctype.getKey()+"]</types><is_facets>1</is_facets><page>0</page><size>0</size></params>";
//    			QueryParam params = ParamsAnalyze.parse(reqParams);
//        		SearchCondition  condition  = params.converToSearchCondition();
                condition.delFilterConditionByFieldName("docType");
                condition.addFilterCondition(new QueryCondition("docType", docType.getKey() + ""));
                condition.setTypes(new String[]{docType.getValue()});
                condition.setSize(0);
                condition.setIndexName("res_xk");
                SearchPager pager = bseService.query(condition);
                map.put(docType.getKey(), pager.getTotal());
            }
            return ResponseModel.ok().setBody(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseModel.fail();
    }

}

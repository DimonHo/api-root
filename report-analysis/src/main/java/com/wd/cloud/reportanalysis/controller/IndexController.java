package com.wd.cloud.reportanalysis.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.reportanalysis.entity.QueryCondition;
import com.wd.cloud.reportanalysis.entity.school.School;
import com.wd.cloud.reportanalysis.service.AnalysisByDBServiceI;
import com.wd.cloud.reportanalysis.service.AnalysisByESServiceI;
import com.wd.cloud.reportanalysis.service.CxfWebServiceI;
import com.wd.cloud.reportanalysis.service.SchoolServiceI;
import com.wd.cloud.reportanalysis.util.ResourceLabel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "数据可视化分析", tags = {""})
@RestController
public class IndexController {
	
	@Autowired
	private SchoolServiceI schoolService;
	
	@Autowired
	private AnalysisByESServiceI analysisByESService;
	
	@Autowired
	private AnalysisByDBServiceI analysisByDBService;
	
	@Autowired
	private CxfWebServiceI cxfWebService;
	
	 @ApiOperation(value = "发文量、分区、被引频次对比分析（非esi）")
	 @ApiImplicitParams({
	            @ApiImplicitParam(name = "act", value = "分析类型（发文量、分区、被引频次）", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "table", value = "表名", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "scid", value = "学校scid", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "compareScids", value = "对比 学校", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "time", value = "时间段", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "source", value = "数据类型", dataType = "String", paramType = "query"),
	            @ApiImplicitParam(name = "signature", value = "机构署名", dataType = "String", paramType = "query")
	    })
	@PostMapping("/compare")
	public ResponseModel compare(HttpServletRequest request) {
		ResourceLabel resource = new ResourceLabel(request);
		Map<String,Object> scidMap = new HashMap<>();
		String type = resource.getType();
		resource.getScids().forEach(scid->{
			School school = schoolService.findByScid(Integer.parseInt(scid));
			if(school == null || school.getIndexName() == null || !type.equals("resourcelabel")) {
				List<QueryCondition> list = resource.getQueryList();
				list.add(new QueryCondition("scid", scid));
				if(resource.getSignature() != null) {
					list.add(new QueryCondition("signature", scid, resource.getSignature()));
				}
				scidMap.put(scid, analysisByESService.amount(list,resource.getFiled(),type));
			} else {
				scidMap.put(scid, cxfWebService.amount(resource.toXML(scid)));
			}
		});
		Map<String,Object> explain = analysisByESService.explain(type);
		Map<String,Object> result= new HashMap<String,Object>();
		result.put("explain", explain);
		result.put("content", scidMap);
		System.out.println(result);
		return ResponseModel.ok().setBody(result);
	}
	
	@ApiOperation(value = "智慧云分析数据：发文量、分区、被引频次对比分析（非esi）")
	@PostMapping("/analysis")
	public ResponseModel analysis(HttpServletRequest request) {
		String act = request.getParameter("act");
		String scid = request.getParameter("scid");
		School school = null;
		if(scid != null) {
			school = schoolService.findByScid(Integer.parseInt(scid));
		}
		String type_c = request.getParameter("type_c");
		String classify = request.getParameter("classify");
		String column = request.getParameter("column");
		String issue = request.getParameter("issue");
		String category = request.getParameter("category");
		String limit_start = request.getParameter("limit_start");
		String limit_num = request.getParameter("limit_num");
		if(limit_start == null) {
			limit_start = "0";
		}
		if(limit_num == null) {
			limit_num = "1";
		}
		if(type_c == null) {
			type_c = "0";
		}
		System.out.println("act:" + act + "scid:" + scid +"classify:" + classify +"column:" + column +"issue:" + issue +"category:" + category+" type_c:" + type_c);
		Map<String,Object> result = new HashMap<>();
		switch (act) {
		case "issue":
			List<Map<String, Object>> list = analysisByDBService.getIssue(Integer.parseInt(limit_start), Integer.parseInt(limit_num));
			if(list != null && list.size()>0) { 
				result.put("content", list.get(0));
			}
			break;
		case "issue_list":
			list = analysisByDBService.getIssue(Integer.parseInt(limit_start), Integer.parseInt(limit_num));
			result.put("content", list);
			break;
		case "category":		//获取优势学科
			result = analysisByDBService.getanalysisCategory("category",issue,school.getName(),Integer.parseInt(scid));
			break;
		case "categoryins":		//获取优势学科
			result = analysisByDBService.getanalysisCategory("categoryins",issue,school.getName(),Integer.parseInt(scid));
			break;
		case "categoryap":
			result = analysisByDBService.getanalysisCategory("categoryap",issue,school.getName(),Integer.parseInt(scid));
			break;
		case "data":
			result = analysisByDBService.analysis(Integer.parseInt(scid), issue, category, classify, column,Integer.parseInt(type_c));
			break;
		case "column_list":
			result.put("content", analysisByDBService.getColumnList(Integer.parseInt(scid), issue));
			break;
		default:
			break;
		}
		
		return ResponseModel.ok().setBody(result);
	}
	 
	 
}

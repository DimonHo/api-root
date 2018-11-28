package org.bse.server.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.bse.server.entity.Scholar;
import org.bse.server.entity.School;
import org.bse.server.service.BseService;
import org.bse.server.service.ScholarService;
import org.bse.server.service.SchoolService;
import org.bse.server.util.ParamsAnalyze;
import org.bse.server.vo.QueryCondition;
import org.bse.server.vo.QueryParam;
import org.bse.server.vo.SearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wd.cloud.commons.model.ResponseModel;

import io.swagger.annotations.Api;

@Api(value = "bse接口", tags = {""})
@RestController
public class IndexController {
	
	@Autowired
	BseService bseService;
	
	@Autowired
	ScholarService scholarService;
	
	@Autowired
	SchoolService schoolService;
	
	
    @RequestMapping("/bse")
    public ResponseModel bse(HttpServletRequest request) {
    	try {
	    	String id = request.getParameter("id");
	    	Scholar scholar = scholarService.findById(Integer.parseInt(id));
	    	
	    	
//	    	if(scholar.getScid() == null || scholar.getScid() <= 0) {
//	    		School school = schoolService.findByName(scholar.getSchool());
//	    		if(school != null) {
//	    			scholar.setScid(school.getScid());
//	    		}
//	    	}
			List<QueryCondition> list = new ArrayList<>();
			list.add(new QueryCondition("authorQuery","author", scholar.getName()));
			
			String schoolSmail = scholar.getSchoolSmail();
			if(StringUtils.isNotBlank(schoolSmail)) {
				String[] schoolSmails = schoolSmail.split(";"); 
				if(schoolSmails.length == 1) {
					list.add(new QueryCondition("orgQuery","org", schoolSmails[0]));
				} else {
					list.add(new QueryCondition("orgQuery","org", schoolSmails[0],2));
					list.add(new QueryCondition("orgQuery","org", schoolSmails[1],2));
				}
			}

			List<String> result = bseService.search(list);
			return ResponseModel.ok().setBody(result);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
		}
        return ResponseModel.fail();
    }
    
    
    @RequestMapping("/bseByAuthor")
    public ResponseModel bseByAuthor(HttpServletRequest request) {
		String author = request.getParameter("author");
		String scid = request.getParameter("scid");
		List<QueryCondition> list = new ArrayList<>();
		list.add(new QueryCondition("authorQuery","author", author));
		list.add(new QueryCondition("commTermsQuery","scids", scid));
		List<String> result = bseService.search(list);
        return ResponseModel.ok().setBody(result);
    }
    
    
    
    @RequestMapping("/search")
    public ResponseModel search(HttpServletRequest request) {
    	try {
	    	String id = request.getParameter("id");
	    	Scholar scholar = scholarService.findById(Integer.parseInt(id));
	    	
	    	
//	    	if(scholar.getScid() == null || scholar.getScid() <= 0) {
//	    		School school = schoolService.findByName(scholar.getSchool());
//	    		if(school != null) {
//	    			scholar.setScid(school.getScid());
//	    		}
//	    	}
			List<QueryCondition> list = new ArrayList<>();
			list.add(new QueryCondition("authorQuery","author", scholar.getName()));
			
			String schoolSmail = scholar.getSchoolSmail();
			if(StringUtils.isNotBlank(schoolSmail)) {
				String[] schoolSmails = schoolSmail.split(";"); 
				if(schoolSmails.length == 1) {
					list.add(new QueryCondition("orgQuery","org", schoolSmails[0]));
				} else {
					list.add(new QueryCondition("orgQuery","org", schoolSmails[0],2));
					list.add(new QueryCondition("orgQuery","org", schoolSmails[1],2));
				}
			}
			SearchCondition condition = new SearchCondition();
			condition.addQueryConditions(list);
			bseService.query(condition);
			return ResponseModel.ok().setBody("");
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
		}
        return ResponseModel.fail();
    }
    

}

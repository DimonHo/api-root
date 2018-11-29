package com.wd.cloud.bse.service;

import java.util.List;

import com.wd.cloud.bse.vo.QueryCondition;
import com.wd.cloud.bse.vo.SearchCondition;


public interface BseService {
	
	public List<String> search(List<QueryCondition> list);
	
	public void query(SearchCondition condition);

}

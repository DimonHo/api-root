package org.bse.server.service;

import java.util.List;

import org.bse.server.vo.QueryCondition;
import org.bse.server.vo.SearchCondition;


public interface BseService {
	
	public List<String> search(List<QueryCondition> list);
	
	public void query(SearchCondition condition);

}

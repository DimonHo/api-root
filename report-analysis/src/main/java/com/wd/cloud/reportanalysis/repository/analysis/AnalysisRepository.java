package com.wd.cloud.reportanalysis.repository.analysis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;




/**
 * @author He Zhigang
 * @param <T>
 * @date 2018/5/7
 * @Description:
 */
@NoRepositoryBean
public interface AnalysisRepository<T, Long extends Serializable> {

	List<Map<String, Object>> getIssue(int limit_start, int limit_num);
	
	public Map<String,Object> getanalysisCategory(String column,String issue,String scname,int scid);
	
	public Map<String, Object> search(int scid,String issue,String category,String classify, String column,int type_c);
	
	public Integer queryCount(String sql);
   
}

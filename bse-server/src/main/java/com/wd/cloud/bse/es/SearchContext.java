package com.wd.cloud.bse.es;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.facet.DefaultFacetConverter;
import com.wd.cloud.bse.util.SpringContextUtil;



/**
 * 检索上下文
 * @author shenfu
 *
 */
@Component
public class SearchContext {
	
//	@Autowired
//	private Map<String,FacetConverter> facetConverts;
	
	public FacetConverter getFacetConvert(String name){
		try {
			FacetConverter convert = (FacetConverter) SpringContextUtil.getBean(name);
			if(convert != null){
				return convert;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new DefaultFacetConverter();
	}

//	public Map<String, FacetConverter> getFacetConverts() {
//		return facetConverts;
//	}
//
//	public void setFacetConverts(Map<String, FacetConverter> facetConverts) {
//		this.facetConverts = facetConverts;
//	}

}

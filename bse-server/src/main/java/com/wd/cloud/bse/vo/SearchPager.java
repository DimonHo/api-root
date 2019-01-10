package com.wd.cloud.bse.vo;

import java.util.List;
import java.util.Map;


import net.sf.json.JSONObject;

public class SearchPager extends Pager{
	
	/**
	 * 统计结果
	 */
	private Map<String,FacetResult> facets;
	
	/**最多显示条数*/
	private int maxSize;
	
	/**耗时*/
	private long cost;
	
	private JSONObject yearRange;
	
	private boolean isBookSearch=false;

//	public Map<String, FacetResult> getFacets() {
//		return facets;
//	}
	public JSONObject getFacets() {
		JSONObject facetsObj = new JSONObject();
		for(Map.Entry<String, FacetResult> facetEntry : facets.entrySet()){
			List<FacetResult.Entry> entries = facetEntry.getValue().getEntries();
			JSONObject subJson = new JSONObject();
			for(FacetResult.Entry entry : entries){
				if(entry.getTerm() != null)
				subJson.put(entry.getTerm(), entry.getCount());
			}
			facetsObj.put(facetEntry.getKey(), subJson);
		}
		return facetsObj;
	}

	public void setFacets(Map<String, FacetResult> facetResults) {
		this.facets = facetResults;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public JSONObject getYearRange() {
		return yearRange;
	}

	public void setYearRange(JSONObject yearRange) {
		this.yearRange = yearRange;
	}

	public boolean isBookSearch() {
		return isBookSearch;
	}

	public void setBookSearch(boolean isBookSearch) {
		this.isBookSearch = isBookSearch;
	}

}

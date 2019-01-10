package com.wd.cloud.bse.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检索条件
 * @author Administrator
 *
 */
public class SearchCondition {
	
	/** 检索的索引*/
	private String indexName;
	
	/**检索的类型*/
	private String[] types;
	
	/**查询条件*/
	private List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
	
	/**过滤条件*/
	private List<QueryCondition> filterConditions = new ArrayList<QueryCondition>();
	
	private List<List<QueryCondition>> filters =  new ArrayList<List<QueryCondition>>();
	
	/**高亮显示字段*/
	private List<String> highLightFields;
	
	private List<FacetField> facetFields = null;
	
	/** 排序字段*/
	private List<SortCondition> sorts;
	
	/**仅统计数据，不获取数据*/
	private boolean facetOnly =false;
	
	/**导出数据*/
	private boolean export = false;
	
	private int from=0;
	
	private int size=10;
	
	private int scid;
	
	private int isLocal;
	
	private Integer isTop = 0;
	
	private Integer isFacets = 0;
	
	private int maxSize=100;
	
	public SearchCondition addQueryCondition(QueryCondition qc){
		this.queryConditions.add(qc);
		return this;
	}
	
	public SearchCondition addFilterCondition(QueryCondition qc){
		this.filterConditions.add(qc);
		return this;
	}
	
	public SearchCondition addHighLight(String field){
		if(highLightFields == null){
			highLightFields = new ArrayList<String>();
		}
		highLightFields.add(field);
		return this;
	}
	
	public SearchCondition addSort(String field,Integer sort){
		if(sorts == null){
			sorts = new ArrayList<>();
		}
		sorts.add(new SortCondition(field,sort));
		return this;
	}
	
	public SearchCondition addSort(String field,String nested,Integer sort){
		if(sorts == null){
			sorts = new ArrayList<>();
		}
		sorts.add(new SortCondition(field,nested,sort));
		return this;
	}
	
	public QueryCondition getFilterConditionByName(String name){
		for(QueryCondition qc : filterConditions){
			if(qc.getFieldFlag().equals(name)){
				return qc;
			}
		}
		return null;
	}
	
	public SearchCondition(){}
	
	public SearchCondition(int scid){
		this.scid = scid;
	}
	
	public SearchCondition(int scid,int uid){
		this.scid = scid;
	}
	
	public SearchCondition(Builder builder){
		this.indexName = builder.indexName ;
		this.types = builder.types;
		this.filterConditions = builder.filterConditions;
		this.highLightFields = builder.highLightFields;
		this.queryConditions = builder.queryConditions;
		this.facetFields = builder.facetFields;
		this.sorts = builder.sorts;
		this.facetOnly = builder.facetOnly;
		this.from = builder.from;
		this.size = builder.size;
		this.size = builder.scid;
	}
	
	/**
	 * SearchCondition 的构建器
	 * @author Administrator
	 *
	 */
	public static class Builder{
		
		private String indexName;
		
		private String[] types;
		
		private List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		
		private List<QueryCondition> filterConditions = new ArrayList<QueryCondition>();
		
		private List<String> highLightFields;
		
		private List<FacetField> facetFields = null;
		
		private List<SortCondition> sorts;
		
		private boolean facetOnly =false;
		
		private int from;
		
		private int size=10;
		
		private int scid;
		
		public Builder setIndexName(String indexName) {
			this.indexName = indexName;
			return this;
		}

		public Builder setTypes(String[] types) {
			this.types = types;
			return this;
		}
		
		public Builder setQueryConditions(List<QueryCondition> queryConditions) {
			this.queryConditions = queryConditions;
			return this;
		}

		public Builder setFilterConditions(List<QueryCondition> filterConditions) {
			this.filterConditions = filterConditions;
			return this;
		}

		public Builder setHighLightFields(List<String> highLightFields) {
			this.highLightFields = highLightFields;
			return this;
		}

		public Builder setFacetFields(List<FacetField> facetFields) {
			this.facetFields = facetFields;
			return this;
		}

		public void setSorts(List<SortCondition> sorts) {
			this.sorts = sorts;
		}

		public Builder setFacetOnly(boolean facetOnly) {
			this.facetOnly = facetOnly;
			return this;
		}

		public Builder setFrom(int from) {
			this.from = from;
			return this;
		}

		public Builder setSize(int size) {
			this.size = size;
			return this;
		}

		public SearchCondition build(){
			return  new SearchCondition(this);
		}
		
		public Builder addQueryCondition(QueryCondition qc){
			this.queryConditions.add(qc);
			return this;
		}
		
		public Builder addFilterCondition(QueryCondition qc){
			this.filterConditions.add(qc);
			return this;
		}
		
		public Builder addHighLight(String field){
			highLightFields.add(field);
			return this;
		}
		
		public Builder addSort(String beanName,String field,Integer sort){
			if(sorts == null){
				sorts = new ArrayList<>();
			}
			sorts.add(new SortCondition(beanName,field,sort));
			return this;
		}

		public Builder setScid(int scid) {
			this.scid = scid;
			return this;
		}

	}
	
	public String getIndexName() {
		return indexName;
	}

	public SearchCondition setIndexName(String indexName) {
		this.indexName = indexName;
		return this;
	}

	public String[] getTypes() {
		return types;
	}

	public SearchCondition setTypes(String[] types) {
		this.types = types;
		return this;
	}

	public List<QueryCondition> getQueryConditions() {
		return queryConditions;
	}

	public void setQueryConditions(List<QueryCondition> queryConditions) {
		this.queryConditions = queryConditions;
	}

	public List<QueryCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(List<QueryCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

	public List<String> getHighLightFields() {
		return highLightFields;
	}

	public void setHighLightFields(List<String> highLightFields) {
		this.highLightFields = highLightFields;
	}

	public int getFrom() {
		return from;
	}

	public SearchCondition setFrom(int from) {
		this.from = from;
		return this;
	}

	public int getSize() {
		return size;
	}

	public SearchCondition setSize(int size) {
		this.size = size;
		return this;
	}

	public List<SortCondition> getSorts() {
		return sorts;
	}

	public void setSorts(List<SortCondition> sorts) {
		this.sorts = sorts;
	}

	public boolean isFacetOnly() {
		return facetOnly;
	}

	public void setFacetOnly(boolean facetOnly) {
		this.facetOnly = facetOnly;
	}

	public List<FacetField> getFacetFields() {
		return facetFields;
	}

	public SearchCondition setFacetFields(List<FacetField> facetFields) {
		this.facetFields = facetFields;
		return this;
	}

	public boolean isExport() {
		return export;
	}

	public void setExport(boolean export) {
		this.export = export;
	}

	public int getScid() {
		return scid;
	}

	public void setScid(int scid) {
		this.scid = scid;
	}

	public int getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(int isLocal) {
		this.isLocal = isLocal;
	}

	public Integer getIsTop() {
		return isTop;
	}

	public void setIsTop(Integer isTop) {
		this.isTop = isTop;
	}

	public Integer getIsFacets() {
		return isFacets;
	}

	public void setIsFacets(Integer isFacets) {
		this.isFacets = isFacets;
	}

	public List<List<QueryCondition>> getFilters() {
		return filters;
	}

	public void setFilters(List<List<QueryCondition>> filters) {
		this.filters = filters;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}


}

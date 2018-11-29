package com.wd.cloud.bse.vo;


/**
 * 分类统计的字段
 * @author Administrator
 *
 */
public class FacetField {
	
	private String name;
	
	private String field;
	
	private String[] fields;
	
	/**需要显示的数量*/
	private int size =10;
	
	/**是否对字段名排序*/
	private boolean order  = false;
	
	/**
	 * 是否按照统计值排序，默认是不排序
	 */
	private boolean orderByCount = false;
	
	private String nested;
	
	public boolean isOrder() {
		return order;
	}

	public void setOrder(boolean order) {
		this.order = order;
	}

	public FacetField(String name,String field,int size,boolean order,boolean orderByCount,String nested){
		this.name = name;
		this.field = field;
		this.size =size;
		this.order = order;
		this.orderByCount =orderByCount;
		this.nested = nested;
	}
	
	public FacetField(String field){
		this.field = field;
	}
	
	public FacetField(String field ,int size){
		this.field = field;
		this.size =size;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isOrderByCount() {
		return orderByCount;
	}

	public void setOrderByCount(boolean orderByCount) {
		this.orderByCount = orderByCount;
	}

	public String getNested() {
		return nested;
	}

	public void setNested(String nested) {
		this.nested = nested;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

}

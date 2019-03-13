package com.wd.cloud.bse.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文档类型
 * @author Administrator
 *
 */
public enum DocType {
	
	/**
	 * 期刊、会议论文、专利、著作、项目、奖励
	 */
	Periodical(1,"proceedings", "periodical"),Proceedings(3,"proceedings", "proceedings"),
	Dissertation(2, "dissertation", "dissertation");
	
	private int key;
	
	/**对应的索引type*/
	private String value;
	
	/**校验类型*/
	private String valiateType;
	
	DocType(int key,String value, String valiateType){
		this.key = key;
		this.value = value;
		this.valiateType = valiateType;
	}
	
	public int getKey(){
		return key;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getValiateType() {
		return valiateType;
	}

	public void setValiateType(String valiateType) {
		this.valiateType = valiateType;
	}

	public static DocType valueOf(int key){
		for(DocType type : DocType.values()){
			if(type.key == key) {
                return type;
            }
		}
		return null;
	}
	
	/**
	 * 从数据源获取DocType
	 * @param source
	 * @return
	 */
	public static DocType getFromSrouce(Map<String,Object> source){
		Object docTypeObj = source.get("docType");
		if(docTypeObj == null){
			List<Map<String,Object>> documents = (List<Map<String,Object>>)source.get("documents");
			if(documents!= null && documents.size() >0){
				Map<String,Object> document = (Map<String,Object>)documents.get(0);
				if(document != null){
					return getFromSrouce(document);
				}
			}
		}
		if(docTypeObj instanceof Integer){
			return valueOf((Integer)docTypeObj);
		}else if(docTypeObj instanceof String){
			return valueOf(Integer.valueOf((String)docTypeObj));
		}else if(docTypeObj instanceof List){
			List<Integer> docTypes = new ArrayList<>();
			for(Object type : (List)docTypeObj){
				docTypes.add(Integer.parseInt(type.toString()));
			}
			if(docTypes.contains(3)){
				return Proceedings;
			}else{
				return valueOf(docTypes.get(0));
			}
		}
		return null;
	}
}

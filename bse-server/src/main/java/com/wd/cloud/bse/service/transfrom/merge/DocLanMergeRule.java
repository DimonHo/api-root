package com.wd.cloud.bse.service.transfrom.merge;

import java.util.List;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class DocLanMergeRule extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "docLanMerge";
	}

	@Override
	protected Integer merge(List<String> dataList, ResourceIndex<Document> resource) {
		return resource.getDocLan();
	}
	
	
	
		
}

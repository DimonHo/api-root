package com.wd.cloud.bse.service.transfrom.merge;

import java.util.List;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class CategoryMergeRule  extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "categoryMerge";
	}

	@Override
	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
		String category = "";
		for (String string : dataList) {
			category = category + ";" + string;
		}
		return category.replaceFirst(";", "");
	}
	
	
}

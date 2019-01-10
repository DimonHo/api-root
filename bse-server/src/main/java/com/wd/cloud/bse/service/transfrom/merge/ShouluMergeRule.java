package com.wd.cloud.bse.service.transfrom.merge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class ShouluMergeRule extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "shouluMerge";
	}

	@Override
	protected List<String> merge(List<String> dataList, ResourceIndex<Document> resource) {
		List<String> list = resource.getShoulu();
		List<String> shoulu = new ArrayList<>();
		if(list != null) {
			for (String string : list) {
				if(StringUtils.isNotEmpty(string)) {
					shoulu.add(string);
				}
			}
		}
		return shoulu;
	}
	
	
	
		
}

package com.wd.cloud.bse.service.transfrom.merge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class SubjectsMergeRule extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "subjectsMerge";
	}

	@Override
	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
		String subjects = "";
		if(dataList != null) {
			for (String string : dataList) {
				if(StringUtils.isNotEmpty(string)) {
					subjects = subjects + ";" + string;
				}
			}
		}
		return subjects.replaceFirst(";", "");
	}
	
	
	
		
}

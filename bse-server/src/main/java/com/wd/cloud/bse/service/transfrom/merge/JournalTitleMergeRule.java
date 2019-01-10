package com.wd.cloud.bse.service.transfrom.merge;

import java.util.List;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class JournalTitleMergeRule extends MergeRule<String> {
	
	@Override
	protected String name() {
		return "journalTitleMerge";
	}

	@Override
	protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
		return resource.getJourTitleFacet();
	}
	
	
	
		
}

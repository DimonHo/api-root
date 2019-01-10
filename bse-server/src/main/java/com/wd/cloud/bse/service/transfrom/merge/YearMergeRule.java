package com.wd.cloud.bse.service.transfrom.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wd.cloud.bse.util.CommUtil;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

public class YearMergeRule extends MergeRule<String> {
	
	private static String spliter = ";";
	
	private static String replace = "#&#&#";
	
	@Override
	protected String name() {
		return "yearMerge";
	}

	@Override
	protected Integer merge(List<String> dataList, ResourceIndex<Document> resource) {
		return resource.getYear();
	}
	
	
	
		
}

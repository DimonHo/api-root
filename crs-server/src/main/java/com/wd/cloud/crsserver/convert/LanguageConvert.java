package com.wd.cloud.crsserver.convert;

import com.weidu.commons.search.AggsResult.Entry;
import com.weidu.commons.search.convert.AggsTermsConvert;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

/**
 * 语言装换
 * 
 * @author shenfu
 *
 */
public class LanguageConvert extends AggsTermsConvert {

	@Override
	public Entry convertTeam(Terms.Bucket entry) {
		int docLan = entry.getKeyAsNumber().intValue();
		switch (docLan){
			case 1: return new Entry("中文", entry.getDocCount(), entry.getKeyAsString());
			case 2: return new Entry("英文", entry.getDocCount(), entry.getKeyAsString());
			default: return new Entry("其它", entry.getDocCount(), entry.getKeyAsString());
		}
	}

}

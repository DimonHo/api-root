package com.wd.cloud.crsserver.convert;

import com.weidu.commons.search.AggsResult.Entry;
import com.weidu.commons.search.convert.AggsTermsConvert;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

/**
 * 收录类型转换
 * 
 * @author Administrator
 *
 */
public class ShouluConvert extends AggsTermsConvert {

	@Override
	public Entry convertTeam(Terms.Bucket bucket) {
		String term = bucket.getKeyAsString();
		switch (term) {
			case "1":
				return new Entry("SCI-E、SSCI、A&HCI", bucket.getDocCount(), term);
			case "2":
				return new Entry("Nature Index", bucket.getDocCount(), term);
			case "3":
				return new Entry("ESI", bucket.getDocCount(), term);
			case "4":
				return new Entry("EI", bucket.getDocCount(), term);
			default:
				return new Entry(term, bucket.getDocCount());
		}

	}

}

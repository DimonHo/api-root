package com.wd.cloud.crsserver.convert;

import com.weidu.commons.search.AggsResult.Entry;
import com.weidu.commons.search.convert.AggsTermsConvert;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

/**
 * ESI高水平论文 统计字段名转换
 * @author Administrator
 *
 */
public class ESIConvert extends AggsTermsConvert {

	@Override
	public Entry convertTeam(Terms.Bucket bucket) {
		String term = bucket.getKeyAsString();
		switch (term) {
			case "1": return new Entry("ESI热点", bucket.getDocCount(), term);
			case "2": return new Entry("ESI高被引", bucket.getDocCount(), term);
			default: return new Entry(term, bucket.getDocCount());
		}
	}
}
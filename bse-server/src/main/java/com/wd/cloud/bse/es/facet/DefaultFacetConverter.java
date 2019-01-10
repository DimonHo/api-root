package com.wd.cloud.bse.es.facet;

import org.apache.commons.lang3.StringUtils;


import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import com.wd.cloud.bse.vo.FacetResult.Entry;

/**
 * 默认的统计转换
 * @author shenfu
 *
 */
public class DefaultFacetConverter extends AbstractFacetConverter{

	@Override
	public Entry convertTeam(Bucket entry) {
		String term = entry.getKeyAsString();
		Entry entry2 = null;
		if (StringUtils.isNoneBlank(term)) {
			entry2 = new Entry(term,entry.getDocCount()); 
		}
		return entry2;
	}

}

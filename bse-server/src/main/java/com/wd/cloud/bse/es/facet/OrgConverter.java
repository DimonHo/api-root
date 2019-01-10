package com.wd.cloud.bse.es.facet;


import java.util.Collection;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.cloud.bse.es.FacetConverter;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.vo.FacetResult;

@Component("scids")
public class OrgConverter implements FacetConverter{
	
	@Autowired
	CacheService cacheService;

	@Override
	public FacetResult convert(Aggregation facet) {
		
		FacetResult result = new FacetResult();
		Terms terms = (Terms) facet;
		Collection<Bucket> bucketColl = (Collection<Bucket>) terms.getBuckets();
		for(Bucket e : bucketColl){
			String scid = e.getKeyAsString();
			String name = cacheService.getSchool(Integer.parseInt(scid));
			FacetResult.Entry entry = new FacetResult.Entry(scid + "#&#&#" + name, e.getDocCount());
			result.addEntry(entry);
		}
		return result;
	}

}

package com.wd.cloud.bse.es.facet;

import com.wd.cloud.bse.es.FacetConverter;
import com.wd.cloud.bse.vo.FacetResult;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractFacetConverter implements FacetConverter{

	@Override
	public FacetResult convert(Aggregation facet){
		FacetResult result = new FacetResult();
		FacetResult.Entry entry;
		Terms terms = (Terms) facet;
		Collection<Bucket> bucketColl = (Collection<Bucket>) terms.getBuckets();
		Iterator<Terms.Bucket> ite = bucketColl.iterator();
		while(ite.hasNext()){
			entry = convertTeam(ite.next());
			if (entry!=null) {
				result.addEntry(entry);
			}			
		}
		return result;
	}
	
	/**
	 * 装换一个具体的Entry
	 * @param entry
	 * @return
	 */
	public abstract FacetResult.Entry convertTeam(Bucket entry);
}

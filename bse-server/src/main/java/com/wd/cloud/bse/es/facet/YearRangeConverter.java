package com.wd.cloud.bse.es.facet;


import com.wd.cloud.bse.es.FacetConverter;
import com.wd.cloud.bse.vo.FacetResult;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("yearRange")
public class YearRangeConverter implements FacetConverter{

	@Override
	public FacetResult convert(Aggregation facet) {
		FacetResult result = new FacetResult();
		String maxYear = null, minYear = null;
		Terms terms = (Terms) facet;
		Collection<Bucket> bucketColl = (Collection<Bucket>) terms.getBuckets();
		for(Bucket e : bucketColl){
			String year = e.getKeyAsString();
			if(maxYear == null || year.compareTo(maxYear)>0){
				maxYear = year;
			}
			if((minYear == null || year.compareTo(minYear)<0) && !"0".equals(year)){
				minYear = year;
			}
		}
		if(maxYear != null){
			FacetResult.Entry minEntry = new FacetResult.Entry(minYear, 0),
				maxEntry  = new FacetResult.Entry(maxYear, 0);
			result.addEntry(minEntry);
			result.addEntry(maxEntry);
		}
		return result;
	}

}

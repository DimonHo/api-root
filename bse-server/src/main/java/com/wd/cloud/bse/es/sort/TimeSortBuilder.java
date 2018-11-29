package com.wd.cloud.bse.es.sort;

import org.apache.commons.lang.StringUtils;
import com.wd.cloud.bse.es.SortBuilderStrategy;
import com.wd.cloud.bse.vo.SortCondition;
import com.wd.cloud.bse.vo.SortEnum;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

@Component("timeSort")
public class TimeSortBuilder implements SortBuilderStrategy{
	
	@Override
	public SortBuilder build(SortCondition sortCondition) {
		FieldSortBuilder builder =  (new FieldSortBuilder(sortCondition.getField()))/*.setNestedFilter(
				FilterBuilders.orFilter(
						FilterBuilders.missingFilter("recordGroup.record.documents.scid"),
						FilterBuilders.termFilter("recordGroup.record.documents.scid", searchCondition.getScid())
				)
		)*/;
		if(StringUtils.isNotBlank(sortCondition.getNested())) {
			builder.setNestedPath(sortCondition.getNested());
		}
		
		if(StringUtils.isNotBlank(sortCondition.getMode())){
			builder.sortMode(SortMode.fromString(sortCondition.getMode()));
		}
		SortOrder order = SortOrder.DESC;
		if(sortCondition.getOrderKey() == SortEnum.asc.value()){
			order = SortOrder.ASC;
			builder.missing("_first");
		}
		builder.order(order);
		return builder;
	}

}

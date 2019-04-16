package com.wd.cloud.bse.es.sort;

import com.wd.cloud.bse.es.SortBuilderStrategyI;
import com.wd.cloud.bse.vo.SortCondition;
import com.wd.cloud.bse.vo.SortEnum;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

@Component("timeSort")
public class TimeSortBuilder implements SortBuilderStrategyI {

    private String field;

    private String nested;

    private String path;

    private String mode;

    @Override
    public SortBuilder build(SortCondition sortCondition) {
        FieldSortBuilder builder = (new FieldSortBuilder(sortCondition.getField()))/*.setNestedFilter(
				FilterBuilders.orFilter(
						FilterBuilders.missingFilter("recordGroup.record.documents.scid"),
						FilterBuilders.termFilter("recordGroup.record.documents.scid", searchCondition.getScid())
				)
		)*/;
        if (StringUtils.isNotBlank(sortCondition.getNested())) {
            builder.setNestedPath(sortCondition.getNested());
        }

        if (StringUtils.isNotBlank(sortCondition.getMode())) {
            builder.sortMode(SortMode.fromString(sortCondition.getMode()));
        }
        SortOrder order = SortOrder.DESC;
        if (sortCondition.getOrderKey() == SortEnum.asc.value()) {
            order = SortOrder.ASC;
            builder.missing("_first");
        }
        builder.order(order);
        return builder;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getNested() {
        return nested;
    }

    public void setNested(String nested) {
        this.nested = nested;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


}

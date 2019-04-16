package com.wd.cloud.bse.es;

/**
 * Facet字段使用的Filter,
 * 1. 不支持nested筛选
 *
 * @author Administrator
 */
public interface FacetFilterBuilder extends FilterBuilderStrategyI {

    /**
     * Nested路径
     *
     * @return
     */
    public String getPath();

}

package org.bse.server.es;

/**
 * Facet不支持嵌套的nested Filter,所以将这些Filter改为Query
 * @author Administrator
 *
 */
public interface FilterQueryBuilder extends IFilterBuilderStrategy{

}

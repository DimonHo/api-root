package org.bse.server.es;

import org.bse.server.vo.SortCondition;
import org.elasticsearch.search.sort.SortBuilder;


public interface SortBuilderStrategy{
	
	public SortBuilder build(SortCondition sortCondition);

}

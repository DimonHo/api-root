package com.wd.cloud.bse.es.facet;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.bse.vo.FacetResult.Entry;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

/**
 * 默认的统计转换
 *
 * @author shenfu
 */
public class DefaultFacetConverter extends AbstractFacetConverter {

    @Override
    public Entry convertTeam(Bucket entry) {
        String term = entry.getKeyAsString();
        Entry entry2 = null;
        if (StrUtil.isNotBlank(term)) {
            entry2 = new Entry(term, entry.getDocCount());
        }
        return entry2;
    }

}

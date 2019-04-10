package com.wd.cloud.bse.service;

import com.wd.cloud.bse.vo.SearchCondition;
import com.wd.cloud.bse.vo.SearchPager;

import java.util.List;


public interface BseService {
    /**
     * 根据学者查询学者成果
     *
     * @param condition
     * @return
     */
    public List<String> searchScholar(SearchCondition condition, String schoolSmail);

    /**
     * 学科列表查询
     *
     * @param condition
     * @return
     */
    public SearchPager query(SearchCondition condition);

    /**
     * 学科详细文章
     *
     * @param ids
     * @param index
     * @return
     */
    public SearchPager getDocByIds(String[] ids, String index);

    /**
     * 最新100条查询
     *
     * @param condition
     * @return
     */
    public <T> SearchPager searchNew(SearchCondition condition);

    /**
     * esi热点前100条查询
     *
     * @param condition
     * @return
     */
    public <T> SearchPager searchEsiHot(SearchCondition condition);

    /**
     * esi高被引前100条查询
     *
     * @param condition
     * @return
     */
    public <T> SearchPager searchEsiTop(SearchCondition condition);


}

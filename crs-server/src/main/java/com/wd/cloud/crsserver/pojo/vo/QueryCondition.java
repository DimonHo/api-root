package com.wd.cloud.crsserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 10:36
 * @Description:
 */
@Data
@Accessors(chain = true)
public class QueryCondition {

    private List<String[]> f;
    /**All 查询*/
    private String q;

    /**DOI*/
    private String d;

    /**PMID*/
    private String p;

    /**Author*/
    private String a;

    /**Journal*/
    private String j;

    /**机构*/
    private String o;

    /**开始年*/
    private Integer sy;

    /**结束年*/
    private Integer ey;

    private String[] k;

    private String facetGroup;

    /**
     * 排序
     */
    private Integer s = SORT_DEFAULT;

    public static final int SORT_DEFAULT = 0;

    /**
     * 最新排序
     */
    public static final int SORT_TIME = 1;

    /**
     * 最热排序
     */
    public static final int SORT_POPULAR = 2;

    /**
     * 时间升序
     */
    public static final int SORT_TIME_ASC=3;

    private static final String[] FIELDS ={"all","doi","pmid","author","journal","org"};

    //private List<QueryField> keywords = new ArrayList<QueryField>();

    private Map<String, Set<String>> filters = new HashMap<String,Set<String>>();

}

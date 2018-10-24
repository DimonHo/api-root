package com.wd.cloud.searchserver.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/9/20
 * @Description: 全局键
 */
public class CommKey {


    public static final String MOBILES = "mobiles/";

    /**
     * 高亮后缀名
     */
    public static final String HIGHLIGHT_FIELD_NAME_SUFFIX = "_highlight";

    public static final List<String> SUBJECT_NO = Arrays.asList("北大核心", "EI", "CSSCI", "ESI");

    public static final List<String> EVAL = Arrays.asList("SJR", "Eigenfactor", "中科院JCR分区(小类)", "中科院JCR分区(大类)", "SCI-E", "SSCI", "A&HCI", "CSCD");
    public static final List<String> SHOU_LU = Arrays.asList("SCI-E", "SSCI", "SJR", "CSCD", "CSSCI", "北大核心");
    public static final List<String> JCR_TYPE = Arrays.asList("中科院JCR分区(大类)", "中科院JCR分区(小类)");
    public static final List<String> JOURNAL_SUBJECT = Arrays.asList("SCI-E", "SSCI", "ESI", "SCOPUS", "CSCD", "CSSCI", "北大核心", "中科院JCR分区(大类)", "中科院JCR分区(小类)", "Eigenfactor");
    public static final List<Integer> VISIT_PAGE = Arrays.asList(10, 20, 30, 50, 70, 100, 200, 250);
    public static final String DEFAULT_TEMP_NAME = "qkdh";
    /**
     * 测试账号
     */
    public static final List TEST_USER = Arrays.asList("spischolar", "xkfwpt");
    /**
     * http://sso.hnlat.com
     */
    public static final String ORG_SESSION_NAME = "login_org";
    public static Map<String, String> EVAL_MAP = new HashMap<String, String>();

    /**
     * "http://cloud.api.hnlat.com"
     */

    static {
        EVAL_MAP.put("SCI-E", "JCR影响因子");
        EVAL_MAP.put("SSCI", "JCR影响因子");
        EVAL_MAP.put("中科院JCR分区(小类)", "中科院JCR分区(小类)");
        EVAL_MAP.put("中科院JCR分区(大类)", "中科院JCR分区(大类)");
        EVAL_MAP.put("SJR", "SJR");
        EVAL_MAP.put("CSCD", "CSCD影响因子");
        EVAL_MAP.put("Eigenfactor", "Eigenfactor");
    }

}

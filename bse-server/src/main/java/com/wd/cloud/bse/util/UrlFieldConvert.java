package com.wd.cloud.bse.util;

import com.wd.cloud.bse.data.Url;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.vo.ArticleSource;
import com.wd.cloud.bse.vo.RuleInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


@Component("urlFieldConvertor")
public class UrlFieldConvert {

    @Autowired
    private CacheService cache;

    public static Map<String, Object> getUrlParams(String param) {

        Map<String, Object> map = new HashMap<String, Object>(0);
        if (param == null) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            } else {
                map.put("key", p[0]);
            }
        }
        return map;
    }

//	public String convert(Url url) {
//		Map<Integer, List<RuleInfo>> ruleMap = cache.getRuleMap();
//		// 如果参数和规则都不为空，则尝试使用规则组装url
//		if (null != ruleMap && !ruleMap.isEmpty()) {
//			// 获取指定的数据库规则
//			List<RuleInfo> docRules = ruleMap.get(url.getSource());
//			if (null != docRules && !docRules.isEmpty()) {
//				// 有配置该数据库下的连接规则，则尝试组装url地址
//				String linkPattern = null;
//				for (RuleInfo lseDocruleConfig : docRules) {
//					String URL = null;
//					linkPattern = lseDocruleConfig.getDocLinkPattern();
//					String param = url.getParam();
//					if(param.contains("http://")) {
//						URL = param;
//						return URL;
//					}
//					URL = composeMessage(linkPattern, getUrlParams(param));
//					if(URL == null) {			//如果没有根据上个规则生成正确url，则用新规则
//						linkPattern = lseDocruleConfig.getNewDocLinkPattern();
//						if(StringUtils.isNotEmpty(linkPattern)) {
//							URL = composeMessage(linkPattern, getUrlParams(param));
//						}
//					}
//					return URL;
//				}
//			}
//		}
//		return null;
//	}

    public static String composeMessage(String urlRule, Map<String, Object> params) {
        boolean isChange = false;
        if (params.isEmpty()) {
            return "";
        }
        Set<Entry<String, Object>> entry = params.entrySet();
        for (Entry<String, Object> entry2 : entry) {
            if (null != urlRule) {
                if (entry2.getValue() == null) {
                    throw new RuntimeException("地址转换参数缺失!");
                }
                if (urlRule.contains(entry2.getKey())) {
                    isChange = true;
                }
                urlRule = urlRule.replace("[" + entry2.getKey() + "]", entry2.getValue().toString());

            }
        }
        if (isChange) {
            return urlRule;
        } else {
            return null;
        }

    }

    public String convert(List<Map<String, Object>> url) {
        Map<Integer, List<RuleInfo>> ruleMap = cache.getRuleMap();
        // 如果参数和规则都不为空，则尝试使用规则组装url
        if (null != ruleMap && !ruleMap.isEmpty()) {
            for (int i = 0; i < url.size(); i++) {
                Map<String, Object> json = url.get(i);
                Integer source = (Integer) json.get("source");
                List<RuleInfo> docRules = ruleMap.get(source);
                if (null != docRules && !docRules.isEmpty()) {
                    // 有配置该数据库下的连接规则，则尝试组装url地址
                    String linkPattern = null;
                    for (RuleInfo lseDocruleConfig : docRules) {
                        String URL = null;
                        linkPattern = lseDocruleConfig.getDocLinkPattern();
                        String param = json.get("param").toString();
                        if (param.contains("http://")) {
                            URL = param;
                            return URL;
                        }
                        URL = composeMessage(linkPattern, getUrlParams(param));
                        if (URL == null) {            //如果没有根据上个规则生成正确url，则用新规则
                            linkPattern = lseDocruleConfig.getNewDocLinkPattern();
                            if (StringUtils.isNotEmpty(linkPattern)) {
                                URL = composeMessage(linkPattern, getUrlParams(param));
                            }
                        }
                        return URL;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 解析来源地址与链接
     *
     * @param url
     * @return
     */
    public ArticleSource convert(Url url) {
        Map<Integer, List<RuleInfo>> ruleMap = cache.getRuleMap();
        // 如果参数和规则都不为空，则尝试使用规则组装url
        if (null != ruleMap && !ruleMap.isEmpty()) {
            // 获取指定的数据库规则
            List<RuleInfo> docRules = ruleMap.get(url.getSource());
            if (null != docRules && !docRules.isEmpty()) {
                // 有配置该数据库下的连接规则，则尝试组装url地址
                String linkPattern = null;
                for (RuleInfo lseDocruleConfig : docRules) {
                    ArticleSource articleSource = new ArticleSource();
                    String URL = null;
                    linkPattern = lseDocruleConfig.getDocLinkPattern();

                    URL = composeMessage(linkPattern, getUrlParams(url.getParam()));
                    if (URL == null) {            //如果没有根据上个规则生成正确url，则用新规则
                        linkPattern = lseDocruleConfig.getNewDocLinkPattern();
                        URL = composeMessage(linkPattern, getUrlParams(url.getParam()));
                    }
                    if (url.getSource() == 10) {
                        URL = WFJournalUrlField.conver(linkPattern, getUrlParams(url.getParam()));
                    }
                    articleSource.setUrl(URL);
                    articleSource.setRuleName(lseDocruleConfig.getRuleName());
                    return articleSource;
                }
            }
        }
        return null;
    }

}

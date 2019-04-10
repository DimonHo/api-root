package com.wd.cloud.bse.service.impl;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.wd.cloud.bse.entity.school.University;
import com.wd.cloud.bse.entity.xk.Issue;
import com.wd.cloud.bse.repository.TransportRepository;
import com.wd.cloud.bse.repository.school.UniversityRepository;
import com.wd.cloud.bse.repository.xk.IssueRepository;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.util.BaseCache;
import com.wd.cloud.bse.vo.RuleInfo;
import com.wd.cloud.bse.vo.SearchCondition;
import com.wd.cloud.bse.vo.SortTools;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
@Scope("singleton")
public class CacheServiceImpl implements CacheService, InitializingBean {

    @Autowired
    TransportRepository transportRepository;
    @Autowired
    IssueRepository issueRepository;
    @Autowired
    UniversityRepository universityRepository;
    private BaseCache cache;

    @PostConstruct
    public void init() {
        //缓存时间24小时
        cache = new BaseCache("es", 1000 * 3600 * 24);
        initRules();
        initIssue();
        initUniversity();
    }

    protected void initRules() {
        SearchCondition condition = new SearchCondition();
        Iterator<SearchHit> ite = transportRepository.query(null, null, null, "yun_datas", "url");
        List<RuleInfo> subList = null;
        ;
        Map<Integer, List<RuleInfo>> ruleMap = new HashMap<Integer, List<RuleInfo>>();
        RuleInfo rule = null;
        Map<String, Object> map = null;
        while (ite.hasNext()) {
            map = ite.next().getSource();
            rule = new RuleInfo();
            rule.setDbId((Integer) map.get("dbId"));
            rule.setBookLinkPattern((String) map.get("bookLinkPattern"));
            rule.setDbLinkUrl((String) map.get("dbLinkUrl"));
            rule.setDocLinkPattern((String) map.get("docLinkPattern"));
            rule.setRuleName((String) map.get("ruleName"));
            rule.setNewDocLinkPattern((String) map.get("newDocLinkPattern"));
            if ("链接地址".equals(map.get("ruleName").toString())) {
                rule.setRuleName("DOI");
            }
            rule.setRuleOrder((Integer) map.get("ruleOrder"));
            subList = ruleMap.get(rule.getDbId());
            if (subList == null) {
                subList = new ArrayList<RuleInfo>();
            }
            subList.add(rule);
            ruleMap.put(rule.getDbId(), subList);
        }
        if (!ruleMap.isEmpty()) {
            cache.put("rules", ruleMap);
        }
    }


    protected void initIssue() {
        List<Issue> list = issueRepository.findAll(SortTools.basicSort("desc", "issue"));
        if (list != null && list.size() > 0) {
            cache.put("issue", list.get(0));
        }
    }

    protected void initUniversity() {
        List<University> list = universityRepository.findAll();
        Map<Integer, String> universityMap = new HashMap<>();
        for (University university : list) {
            universityMap.put(university.getId(), university.getName());
        }
        cache.put("university", universityMap);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<RuleInfo>> getRuleMap() {
        Map<Integer, List<RuleInfo>> rules = null;
        try {
            rules = (Map<Integer, List<RuleInfo>>) cache.get("rules");
        } catch (NeedsRefreshException e) {//需要刷新缓存
            e.printStackTrace();
            initRules();
            return getRuleMap();
        }
        return rules;
    }

    @Override
    public Issue getIssue() {
        Issue issue = null;
        try {
            issue = (Issue) cache.get("issue");
        } catch (NeedsRefreshException e) {//需要刷新缓存
            e.printStackTrace();
            initIssue();
            return getIssue();
        }
        return issue;
    }

    @Override
    public String getEsiIssue() {
        String esiIssue = null;
        try {
            Issue issue = (Issue) cache.get("issue");
            esiIssue = issue.getEsiIssue().replace(".", "");
        } catch (NeedsRefreshException e) {//需要刷新缓存
            e.printStackTrace();
            initIssue();
            return getEsiIssue();
        }
        return esiIssue;
    }

    @Override
    public String getSchool(int id) {
        String name = null;
        try {
            Map<Integer, String> universityMap = (Map<Integer, String>) cache.get("university");
            name = universityMap.get(id);
        } catch (NeedsRefreshException e) {//需要刷新缓存
            e.printStackTrace();
            initUniversity();
            return getSchool(id);
        }
        return name;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void cache(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getCache(String key) {
        try {
            return cache.get(key);
        } catch (NeedsRefreshException e) {
            return null;
        }
    }

    @Override
    public void flushAll() {
        cache.flushAll();
    }
}

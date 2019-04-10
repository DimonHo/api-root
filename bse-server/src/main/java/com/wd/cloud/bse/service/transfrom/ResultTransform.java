package com.wd.cloud.bse.service.transfrom;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.Periodical;
import com.wd.cloud.bse.data.ResourceIndex;
import com.wd.cloud.bse.data.Url;
import com.wd.cloud.bse.es.FacetConverter;
import com.wd.cloud.bse.es.SearchContext;
import com.wd.cloud.bse.service.CacheService;
import com.wd.cloud.bse.service.transfrom.merge.ResourceMerger;
import com.wd.cloud.bse.util.TypeUtils;
import com.wd.cloud.bse.util.UrlFieldConvert;
import com.wd.cloud.bse.vo.*;
import com.wd.cloud.bse.vo.FacetResult.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 返回数据解析
 *
 * @author Administrator
 */
@Component
public class ResultTransform {

    @Autowired
    UrlFieldConvert urlFieldConvertor;

    @Autowired
    SearchContext context;

    @Autowired
    CacheService cacheService;

    @Autowired
    SourceTransfrom sourceTransfrom;

    /**
     * 学科平台专家学者人物成果--结果解析
     *
     * @param searchResponse
     * @return
     */
    public List<String> transformScholar(SearchResponse searchResponse, String schoolSmail) {
        List<String> result = new ArrayList<>();
        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit hit : searchHits.getHits()) {
            Map<String, Object> source = hit.getSource();
            List<Map<String, Object>> list = (List<Map<String, Object>>) source.get("documents");
            Map<String, Object> map = new HashMap<>();
            Map<Integer, Object> urlMap = new TreeMap<Integer, Object>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> json = list.get(i);
                String org = json.get("org").toString();
                boolean isOrg = false;
                String[] schoolSmails = schoolSmail.split(";");
                //如果学校不匹配，则跳过，如查询中南大学，会查出中南林业科技大学，需要剔除
                for (String school : schoolSmails) {
                    if (org.contains(school)) {
                        isOrg = true;
                    }
                }
                if (!isOrg) {
                    break;
                }
                // 遍历map中的键
                for (String key : json.keySet()) {
                    if (json.get(key) == null) {
                        continue;
                    }
                    String value = json.get(key).toString();
                    if ("url".equals(key)) {
                        List<Map<String, Object>> url = (List<Map<String, Object>>) json.get(key);
                        String linkUrl = urlFieldConvertor.convert(url);
                        if (linkUrl != null) {
                            if (linkUrl.contains("wanfangdata")) {
                                urlMap.put(1, linkUrl);
                            } else if (linkUrl.contains("cqvip")) {
                                urlMap.put(2, linkUrl);
                            } else if (linkUrl.contains("cnki")) {
                                urlMap.put(3, linkUrl);
                            } else {
                                urlMap.put(5, linkUrl);
                            }

                        }
                    } else if ("doi".equals(key) && StringUtils.isNotEmpty(json.get(key).toString())) {
                        String linkUrl = "http://doi.org/" + json.get(key).toString();
                        urlMap.put(4, linkUrl);
                    } else if (StringUtils.isNotEmpty(value)) {
                        map.put(key, value.split("#&#&#")[0]);
                    }
                }
            }
            try {
                Iterator<Integer> iter = urlMap.keySet().iterator();
                String url = null;
                String author = "";
                String docTitle = "";
                String journalTitle = "";
                String year = "";
                String number = "";
                String pageRange = "";
                StringBuffer sb = new StringBuffer();
                if (map.containsKey("author")) {
                    author = map.get("author").toString();
                    sb.append(author);
                }
                while (iter.hasNext()) {
                    Integer key = iter.next();
                    url = urlMap.get(key).toString();
                    System.out.println(key + ":" + urlMap.get(key));
                    break;
                }
                if (map.containsKey("docTitle")) {
                    docTitle = map.get("docTitle").toString();
                    if (url != null) {
                        sb.append("<a target=\"_blank\" href=\"" + url + "\">");
                        sb.append(docTitle);
                        sb.append("</a>");
                    } else {
                        sb.append(docTitle);
                    }
                }
                sb.append("[J].");
                if (map.containsKey("journalTitle")) {
                    journalTitle = map.get("journalTitle").toString();
                    sb.append(journalTitle + ".");
                }
                if (map.containsKey("year")) {
                    year = map.get("year").toString();
                    sb.append(year);
                }
                if (map.containsKey("number")) {
                    number = map.get("number").toString();
                    sb.append("(" + number + ").");
                }
                if (map.containsKey("pageRange")) {
                    pageRange = map.get("pageRange").toString();
                    sb.append(pageRange);
                }

                while (iter.hasNext()) {
                    Integer key = iter.next();
                    url = urlMap.get(key).toString();
                    System.out.println(key + ":" + urlMap.get(key));
                    break;
                }
                if (!StringUtils.isEmpty(journalTitle)) {        //先只放期刊
                    result.add(sb.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 学科结果解析
     *
     * @param searchResponse
     * @param condition
     * @return
     */
    public SearchPager transform(SearchResponse searchResponse, SearchCondition condition) {
        SearchPager pager = new SearchPager();
        SearchHits searchHits = searchResponse.getHits();
        pager.setTotal((int) searchHits.getTotalHits());
        ResourceMerger ResourceMerger = new ResourceMerger();
        List<Document> list = new ArrayList<>();
        for (SearchHit hit : searchHits.getHits()) {
            Map<String, Object> source = hit.getSource();
            ResourceIndex<Document> resourceIndex = TypeUtils.hitMapper(hit);
            try {
                Document doc = (Document) ResourceMerger.merge(resourceIndex);
                doc.setDocTypes(resourceIndex.getDocType());
                doc.setId(hit.getId());
                Map<String, Object> dctl = new HashMap<>();
                dctl.put("GB/T7714", sourceTransfrom.getGB(doc, doc.getAuthor()).replaceAll("'", "\""));
                dctl.put("APA", sourceTransfrom.getAPA(doc, doc.getAuthor()).replaceAll("'", "\""));
                dctl.put("MLA", sourceTransfrom.getMLA(doc, doc.getAuthor()).replaceAll("'", "\""));
                doc.setSource(dctl);
                regulate(doc, resourceIndex.getEsiIssue());
                List<Url> urls = doc.getUrl();
                if (urls != null) {            //解析来源地址与链接
                    List<ArticleSource> links = new ArrayList<ArticleSource>();
                    Set<String> urlSet = new HashSet<>();
                    for (Url url : urls) {
                        ArticleSource link = urlFieldConvertor.convert(url);
                        if (link != null) {
                            if (!urlSet.contains(link.getUrl())) {
                                links.add(link);
                            }
                            urlSet.add(link.getUrl());
                        }
                    }
                    doc.setLinkUrl(JSONArray.fromObject(links));
                }
                list.add(doc);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        pager.setRows(list);
        Map<String, FacetResult> facetResults = new HashMap<String, FacetResult>();
        if (searchResponse.getAggregations() == null) {
            pager.setFacets(facetResults);
            return pager;
        }
        Map<String, Aggregation> aggs = searchResponse.getAggregations().getAsMap();
        for (Map.Entry<String, Aggregation> entry : aggs.entrySet()) {
            Aggregation agg = entry.getValue();
            facetConvert(agg, facetResults);
        }

        FacetResult esiShoulu = new FacetResult(), esiIssueShoulu = facetResults.get("esiIssue"), shoulu = facetResults.get("shoulu"),
                yearRange = facetResults.get("yearRange"), scids = facetResults.get("scids");
        Set<String> esiIssues = new HashSet<>();
        if (esiIssues.size() == 0) {            //获取esiIssues的最新期
            esiIssues.add(cacheService.getEsiIssue());
        }
        if (esiIssueShoulu != null) {        //只取esiIssues最新期作为聚合结果
            for (Entry entry : esiIssueShoulu.getEntries()) {
                String[] items = entry.getTerm().toString().split("\\^");
                if (items.length == 2) {
                    if (esiIssues.contains(items[0])) {
                        esiShoulu.addEntry(items[1], entry.getCount());
                    }
                }
            }
        }
        if (shoulu == null) {
            facetResults.put("shoulu", esiShoulu);
        } else {
            if (esiShoulu != null) {        //将esiIssue和shoulu字段的聚合合并成一项
                esiShoulu.getEntries().addAll(shoulu.getEntries());
                esiShoulu.getEntries().sort(new Comparator<Entry>() {
                    @Override
                    public int compare(Entry o1, Entry o2) {
                        int index1 = 1, index2 = 2;
                        for (int i = 0; i < LatConstant.SHOULUS.length; i++) {
                            String sl = LatConstant.SHOULUS[i];
                            if (sl.equals(o1.getTerm())) {
                                index1 = i;
                            }
                            if (sl.equals(o2.getTerm())) {
                                index2 = i;
                            }
                        }
                        return index1 - index2;
                    }
                });
                facetResults.put("shoulu", esiShoulu);
                shoulu.getEntries().addAll(esiShoulu.getEntries());
            }
        }
        pager.setFacets(facetResults);
        JSONObject yearRangeObj = new JSONObject();
        if (yearRange != null && yearRange.getEntries().size() > 1) {
            yearRangeObj.put("all", new String[]{yearRange.getEntries().get(0).getTerm(), yearRange.getEntries().get(1).getTerm()});
        }
        pager.setYearRange(yearRangeObj);
        return pager;
    }

    /**
     * 聚合结果解析
     *
     * @param agg
     * @param facetResults
     */
    private void facetConvert(Aggregation agg, final Map<String, FacetResult> facetResults) {
        if (agg instanceof Nested || agg instanceof Filter) {
            Map<String, Aggregation> aggs = ((SingleBucketAggregation) agg).getAggregations().getAsMap();
            for (Map.Entry<String, Aggregation> entry : aggs.entrySet()) {
                facetConvert(entry.getValue(), facetResults);
            }
        } else if (agg instanceof Terms) {
            FacetConverter convert = context.getFacetConvert(agg.getName());
            FacetResult facetResult = convert.convert(agg);
            facetResults.put(agg.getName(), facetResult);
        }
    }

    private <T> void regulate(T doc, List<String> esiIssue) {
        String lastEsiIssue = cacheService.getEsiIssue();
        if (doc instanceof Periodical) {
            Periodical periodical = (Periodical) doc;
            if (esiIssue != null && esiIssue.size() > 0) {
                Set<String> suluSet = new HashSet<>();
                String[] items;
                for (String sulu : esiIssue) {
                    items = sulu.split("\\^");
                    if (items.length == 2) {
                        if (lastEsiIssue == null || lastEsiIssue.contains(items[0])) {
                            suluSet.add(items[1]);
                        }
                    }
                }
                if (suluSet.size() > 0) {
                    periodical.addShulu(suluSet);
                }
            }
        }

    }


}

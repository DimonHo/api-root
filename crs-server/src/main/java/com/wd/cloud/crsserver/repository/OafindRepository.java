package com.wd.cloud.crsserver.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.crsserver.pojo.document.Oafind;
import com.weidu.commons.search.SearchField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:42
 * @Description:
 */
@Resource
public interface OafindRepository extends ElasticsearchRepository<Oafind, String> {

    Page<Oafind> findByDocumentType(String documentType, Pageable pageable);

    class Template {

        /**
         * 精确查询
         *
         * @param searchField
         * @return
         */
        public static SearchQuery exactQuery(List<SearchField> searchField, Pageable pageable) {
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            searchField.forEach(field -> {
                switch (field.getFieldName()) {
                    case "all":
                        MultiMatchQueryBuilder multiQueryBuilder = QueryBuilders.multiMatchQuery(field.getValue())
                                .field("title", 2)
                                .field("abstractInfo")
                                .field("doi")
                                .field("journal.name")
                                .field("journal.nameAbb")
                                .field("affiliationAnalyzed")
                                .field("keywords", 2)
                                .field("classic", 2)
                                .field("authorAnalyzed")
                                .field("authorFacets", 10)
                                .minimumShouldMatch("80%").tieBreaker(0.3f);
                        boolQueryBuilder.must(multiQueryBuilder);
                        break;
                    case "keyword":
                        BoolQueryBuilder keywordQueryBuilder = QueryBuilders.boolQuery()
                                .should(QueryBuilders.queryStringQuery(field.getValue()).field("keywords").minimumShouldMatch("100%"))
                                .should(QueryBuilders.matchPhraseQuery("keywords", field.getValue()));
                        boolQueryBuilder.must(keywordQueryBuilder);
                        break;
                    case "title":
                        boolQueryBuilder.must(QueryBuilders.queryStringQuery(field.getValue()).field("title").minimumShouldMatch("80%"));
                        break;
                    case "doi":
                        BoolQueryBuilder doiQueryBuilder = QueryBuilders.boolQuery()
                                .should(QueryBuilders.prefixQuery("doi", field.getValue()))
                                .should(QueryBuilders.termQuery("doi", field.getValue()))
                                .minimumShouldMatch(1);
                        boolQueryBuilder.must(doiQueryBuilder);
                        nativeSearchQueryBuilder.withHighlightFields(highlightField("doi"));
                        break;
                    case "author":
                        BoolQueryBuilder authorQueryBuilder = QueryBuilders.boolQuery()
                                .should(QueryBuilders.queryStringQuery(field.getValue()).field("authorAnalyzed"))
                                .should(QueryBuilders.termQuery("authorFacets", field.getValue()))
                                .minimumShouldMatch(1);
                        boolQueryBuilder.must(authorQueryBuilder);
                        break;
                    case "journal":
                        BoolQueryBuilder journalQueryBuilder = QueryBuilders.boolQuery()
                                .should(QueryBuilders.queryStringQuery(field.getValue()).field("journal.name").field("journal.nameAbb"))
                                .should(QueryBuilders.matchPhraseQuery("journal.name", field.getValue()).boost(5.0f))
                                .should(QueryBuilders.matchPhraseQuery("journal.nameAbb", field.getValue()).boost(5.0f))
                                .minimumShouldMatch(1);
                        boolQueryBuilder.must(journalQueryBuilder);
                        break;
                    case "year":
                        // 年份范围格式必须是 “2000-2019”
                        assert StrUtil.contains(field.getValue(), '-');
                        Integer start = StrUtil.startWith(field.getValue(), '-') ? null : Integer.valueOf(field.getValue().split("-")[0]);
                        Integer end = StrUtil.endWith(field.getValue(), '-') ? null : Integer.valueOf(field.getValue().split("-")[1]);
                        RangeQueryBuilder yearQueryBuilder = QueryBuilders.rangeQuery("year");
                        if (start != null) {
                            yearQueryBuilder.from(start, true);
                        }
                        if (end != null) {
                            yearQueryBuilder.to(end, true);
                        }
                        boolQueryBuilder.filter(yearQueryBuilder);
                        break;
                    case "language":
                        boolQueryBuilder.filter(QueryBuilders.termQuery("language", field.getValue()));
                        break;
                    default:
                        BoolQueryBuilder defaultQueryBuilder = QueryBuilders.boolQuery()
                                .should(QueryBuilders.queryStringQuery(field.getValue()).field(field.getFieldName()).minimumShouldMatch("100%"));
                        boolQueryBuilder.must(defaultQueryBuilder);
                }
            });

            return nativeSearchQueryBuilder.withQuery(boolQueryBuilder)
                    .withHighlightFields(highlightField("title"), highlightField("abstractInfo"))
                    .withPageable(pageable)
                    .build();
        }


        /**
         * 模糊查询
         *
         * @param queryStr  查询关键字
         * @param startYear 开始年份
         * @param endYear   结束年份
         * @param codes     学科类别
         * @param sources   来源
         * @param years     年份列表
         * @param journals  期刊名称
         * @param languages 语种
         * @param aggFiled  聚合统计字段
         * @param pageable
         * @return
         */
        public static SearchQuery builders(String queryStr,
                                           Integer startYear, Integer endYear,
                                           List<Integer> codes,
                                           List<String> sources,
                                           List<Integer> years,
                                           List<String> journals,
                                           List<Integer> languages,
                                           String aggFiled,
                                           Pageable pageable) {

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            // 查询关键字
            if (StrUtil.isNotBlank(queryStr)) {
                MultiMatchQueryBuilder multiQueryBuilder = QueryBuilders.multiMatchQuery(queryStr)
                        .field("title", 2)
                        .field("abstractInfo")
                        .field("doi")
                        .field("journal.name")
                        .field("journal.nameAbb")
                        .field("affiliationAnalyzed")
                        .field("keywords", 2)
                        .field("classic", 2)
                        .field("authorAnalyzed")
                        .field("authorFacets", 10)
                        .minimumShouldMatch("80%").tieBreaker(0.3f);
                boolQueryBuilder.must(multiQueryBuilder);
            }


            RangeQueryBuilder rangeYearQueryBuilder = QueryBuilders.rangeQuery("year");
            if (startYear != null || endYear != null) {
                if (startYear != null) {
                    rangeYearQueryBuilder.from(startYear, true);
                }
                if (endYear != null) {
                    rangeYearQueryBuilder.to(endYear, true);
                }
                boolQueryBuilder.filter(rangeYearQueryBuilder);
            }

            BoolQueryBuilder codeQueryBuilder = QueryBuilders.boolQuery();
            if (CollectionUtil.isNotEmpty(codes)) {
                codes.forEach(code -> codeQueryBuilder.should(QueryBuilders.termQuery("code", code)));
                boolQueryBuilder.must(codeQueryBuilder);
            }

            BoolQueryBuilder sourceQueryBuilder = QueryBuilders.boolQuery();
            if (CollectionUtil.isNotEmpty(sources)) {
                sources.forEach(source -> sourceQueryBuilder.should(QueryBuilders.termQuery("source.name", source)));
                boolQueryBuilder.must(sourceQueryBuilder);
            }

            BoolQueryBuilder yearQueryBuilder = QueryBuilders.boolQuery();
            if (CollectionUtil.isNotEmpty(years)) {
                years.forEach(year -> yearQueryBuilder.should(QueryBuilders.termQuery("year", year)));
                boolQueryBuilder.must(yearQueryBuilder);
            }

            BoolQueryBuilder journalQueryBuilder = QueryBuilders.boolQuery();
            if (CollectionUtil.isNotEmpty(journals)) {
                journals.forEach(journal -> journalQueryBuilder.should(QueryBuilders.termQuery("journal.nameFacet", journal)));
                boolQueryBuilder.must(journalQueryBuilder);
            }

            BoolQueryBuilder languageQueryBuilder = QueryBuilders.boolQuery();
            if (CollectionUtil.isNotEmpty(languages)) {
                languages.forEach(language -> languageQueryBuilder.should(QueryBuilders.termQuery("language", language)));
                boolQueryBuilder.must(languageQueryBuilder);
            }

            if (StrUtil.isNotBlank(aggFiled)) {
                TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(aggFiled).field(aggFiled);
                nativeSearchQueryBuilder.addAggregation(termsAggregationBuilder);
            }

            return nativeSearchQueryBuilder.withQuery(boolQueryBuilder)
                    .withHighlightFields(highlightField("title"), highlightField("abstractInfo"))
                    .withPageable(pageable)
                    .build();

        }


        /**
         * 高亮字段
         *
         * @param field
         * @return
         */
        private static HighlightBuilder.Field highlightField(String field) {
            return new HighlightBuilder
                    .Field(field)
                    .preTags("<font class='wd_hightlight'>")
                    .postTags("</font>")
                    .fragmentSize(500)
                    .numOfFragments(5);
        }

    }
}

package com.wd.cloud.pdfsearchserver.model;

import lombok.Data;

@Data
public class LiteratureModel {

    /**
     * 文献标题
     */
    private String docTitle;

    /**
     * doi
     */
    private String doi;

    private String issn;

    private String issue;

    private String volume;

    /**
     * 发表年份
     */
    private String year;

    /**
     * 文献作者
     */
    private String author;

    private String journal;


}

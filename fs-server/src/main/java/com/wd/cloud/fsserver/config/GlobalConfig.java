package com.wd.cloud.fsserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2018/7/20
 * @Description:
 */
@Component
@ConfigurationProperties(value = "global")
public class GlobalConfig {

    private String rootPath;

    private String hbaseStore = "fsStore";

    private String hbaseImage = "fsImage";

    private String hbaseDoc = "fsDoc";

    private String hbasePaper = "fsPaper";

    public String getHbaseStore() {
        return hbaseStore;
    }

    public GlobalConfig setHbaseStore(String hbaseStore) {
        this.hbaseStore = hbaseStore;
        return this;
    }

    public String getHbaseImage() {
        return hbaseImage;
    }

    public GlobalConfig setHbaseImage(String hbaseImage) {
        this.hbaseImage = hbaseImage;
        return this;
    }

    public String getHbaseDoc() {
        return hbaseDoc;
    }

    public GlobalConfig setHbaseDoc(String hbaseDoc) {
        this.hbaseDoc = hbaseDoc;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public GlobalConfig setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}

package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 产品表
 */
@Entity
@Table(name = "product")
public class Product extends AbstractEntity {

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品官网url
     */
    private String url;

    @OneToMany(mappedBy = "product")
    private List<OrgProduct> orgProducts;

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Product setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<OrgProduct> getOrgProducts() {
        return orgProducts;
    }

    public Product setOrgProducts(List<OrgProduct> orgProducts) {
        this.orgProducts = orgProducts;
        return this;
    }
}

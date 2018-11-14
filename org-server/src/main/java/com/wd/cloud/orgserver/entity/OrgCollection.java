package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2018/11/7
 * @Description: 机构馆藏数据库
 */
@Entity
@Table(name = "org_collection")
public class OrgCollection extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Org org;
    /**
     * 馆藏数据库
     */
    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;
    /**
     * 资源本地地址
     */
    private String localUrl;

    /**
     * 是否显示馆藏数据库
     */
    private boolean display;

    public Org getOrg() {
        return org;
    }

    public OrgCollection setOrg(Org org) {
        this.org = org;
        return this;
    }

    public Collection getCollection() {
        return collection;
    }

    public OrgCollection setCollection(Collection collection) {
        this.collection = collection;
        return this;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public OrgCollection setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
        return this;
    }

    public boolean isDisplay() {
        return display;
    }

    public OrgCollection setDisplay(boolean display) {
        this.display = display;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrgCollection that = (OrgCollection) o;

        if (display != that.display) {
            return false;
        }
        if (org != null ? !org.equals(that.org) : that.org != null) {
            return false;
        }
        if (collection != null ? !collection.equals(that.collection) : that.collection != null) {
            return false;
        }
        return localUrl != null ? localUrl.equals(that.localUrl) : that.localUrl == null;
    }

    @Override
    public int hashCode() {
        int result = org != null ? org.hashCode() : 0;
        result = 31 * result + (collection != null ? collection.hashCode() : 0);
        result = 31 * result + (localUrl != null ? localUrl.hashCode() : 0);
        result = 31 * result + (display ? 1 : 0);
        return result;
    }
}

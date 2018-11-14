package com.wd.cloud.orgserver.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/11/7
 * @Description: 馆藏数据库
 */
@Entity
@Table(name = "collection")
public class Collection extends AbstractEntity {
    /**
     * 馆藏数据库名称
     */
    private String name;

    private String url;

    @OneToMany(mappedBy = "collection")
    private Set<OrgCollection> orgCollections;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "org_setting_collection",
            joinColumns = {@JoinColumn(name = "org_setting_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "collection_id", referencedColumnName ="id")})
    private Set<OrgSetting> orgSettings;

    public String getName() {
        return name;
    }

    public Collection setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Collection setUrl(String url) {
        this.url = url;
        return this;
    }

    public Set<OrgCollection> getOrgCollections() {
        return orgCollections;
    }

    public Collection setOrgCollections(Set<OrgCollection> orgCollections) {
        this.orgCollections = orgCollections;
        return this;
    }

    public Set<OrgSetting> getOrgSettings() {
        return orgSettings;
    }

    public Collection setOrgSettings(Set<OrgSetting> orgSettings) {
        this.orgSettings = orgSettings;
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

        Collection that = (Collection) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        return orgCollections != null ? orgCollections.equals(that.orgCollections) : that.orgCollections == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (orgCollections != null ? orgCollections.hashCode() : 0);
        return result;
    }
}

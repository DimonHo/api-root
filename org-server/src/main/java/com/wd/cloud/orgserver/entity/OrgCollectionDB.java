package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/11/7
 * @Description: 机构馆藏数据库
 */
@Entity
@Table(name = "org_collection_db")
public class OrgCollectionDB extends AbstractEntity{

    @ManyToOne
    private OrgInfo orgInfo;
    /**
     * 馆藏数据库
     */
    @ManyToOne
    private Set<CollectionDB> collectionDBs;
    /**
     * 资源本地地址
     */
    private String localUrl;

    private boolean show;


}

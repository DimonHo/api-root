package com.wd.cloud.orgserver.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构产品信息
 */
@Entity
@Table(name = "org_product")
public class OrgProduct extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Org org;

    /**
     * 产品
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 过期时间
     */
    private Date endDate;

    /**
     * 状态  1购买、2试用、0停用
     */
    private Integer status;

    public Org getOrg() {
        return org;
    }

    public OrgProduct setOrg(Org org) {
        this.org = org;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public OrgProduct setProduct(Product product) {
        this.product = product;
        return this;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public OrgProduct setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public OrgProduct setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public OrgProduct setStatus(Integer status) {
        this.status = status;
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

        OrgProduct that = (OrgProduct) o;

        if (org != null ? !org.equals(that.org) : that.org != null) {
            return false;
        }
        if (product != null ? !product.equals(that.product) : that.product != null) {
            return false;
        }
        if (beginDate != null ? !beginDate.equals(that.beginDate) : that.beginDate != null) {
            return false;
        }
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) {
            return false;
        }
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = org != null ? org.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (beginDate != null ? beginDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}

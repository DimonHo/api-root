package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.OrgProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgProductRepository extends JpaRepository<OrgProduct, Long> {

    /**
     * 机构所有产品
     * @param orgFlag
     * @return
     */
    List<OrgProduct> findByOrgFlag(String orgFlag);

    /**
     * 查询机构过期产品
     * @param orgFlag
     * @param effDate
     * @return
     */
    List<OrgProduct> findByOrgFlagAndExpDateBefore(String orgFlag, Date effDate);

    /**
     * 查询机构未过期产品
     * @param orgFlag
     * @param effDate
     * @param expDate
     * @return
     */
    List<OrgProduct> findByOrgFlagAndEffDateBeforeAndExpDateAfter(String orgFlag,Date effDate, Date expDate);

    /**
     * 查询机构产品
     * @param orgFlag
     * @param productId
     * @return
     */
    Optional<OrgProduct> findByOrgFlagAndProductId(String orgFlag, Long productId);


    /**
     * 未过期的产品
     * @param orgFlag
     * @param status
     * @return
     */
    @Query(value = "select * from org_product where org_flag =?1 and status =?2 and to_days(end_date) > to_days(now())",nativeQuery = true)
    List<OrgProduct> findByOrgFlagAndStatus(String orgFlag,Integer status);

    /**
     * 过期产品
     * @param orgFlag
     * @return
     */
    @Query(value = "select * from org_product where org_flag =?1 and to_days(end_date) < to_days(now())",nativeQuery = true)
    List<OrgProduct> expByOrgFlag(String orgFlag);

    /**
     * 取消订购某产品
     * @param orgFlag
     */
    void deleteByOrgFlagAndProductIdIn(String orgFlag,List<Long> productIds);

    /**
     * 取消订购所有产品
     * @param orgFlag
     */
    void deleteByOrgFlag(String orgFlag);

    void deleteByOrgFlagAndProductId(String orgFlag,Long productId);


}

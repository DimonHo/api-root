package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.OrgProd;
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
public interface OrgProdRepository extends JpaRepository<OrgProd, Long> {

    /**
     * 机构所有产品
     *
     * @param orgFlag
     * @return
     */
    List<OrgProd> findByOrgFlag(String orgFlag);

    /**
     * 查询机构过期产品
     *
     * @param orgFlag
     * @param effDate
     * @return
     */
    List<OrgProd> findByOrgFlagAndExpDateBefore(String orgFlag, Date effDate);

    /**
     * 查询机构未过期产品
     *
     * @param orgFlag
     * @param effDate
     * @param expDate
     * @return
     */
    List<OrgProd> findByOrgFlagAndEffDateBeforeAndExpDateAfter(String orgFlag, Date effDate, Date expDate);

    /**
     * 查询机构产品
     *
     * @param orgFlag
     * @param prodId
     * @return
     */
    Optional<OrgProd> findByOrgFlagAndProdId(String orgFlag, Long prodId);


    /**
     * 未过期的产品
     *
     * @param orgFlag
     * @param status
     * @return
     */
    @Query(value = "select * from org_prod where org_flag =?1 and status =?2 and to_days(end_date) > to_days(now())", nativeQuery = true)
    List<OrgProd> findByOrgFlagAndStatus(String orgFlag, Integer status);

    /**
     * 过期产品
     *
     * @param orgFlag
     * @return
     */
    @Query(value = "select * from org_prod where org_flag =?1 and to_days(end_date) < to_days(now())", nativeQuery = true)
    List<OrgProd> expByOrgFlag(String orgFlag);

    /**
     * 取消订购某产品
     *
     * @param orgFlag
     */
    void deleteByOrgFlagAndProdIdIn(String orgFlag, List<Long> prodIds);

    /**
     * 取消订购所有产品
     *
     * @param orgFlag
     */
    void deleteByOrgFlag(String orgFlag);

    /**
     * @param orgFlag
     * @param prodId
     */
    void deleteByOrgFlagAndProdId(String orgFlag, Long prodId);


}

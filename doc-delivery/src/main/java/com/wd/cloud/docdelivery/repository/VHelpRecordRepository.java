package com.wd.cloud.docdelivery.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.docdelivery.entity.VHelpRecord;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface VHelpRecordRepository extends JpaRepository<VHelpRecord, Long>, JpaSpecificationExecutor<VHelpRecord> {

    List<VHelpRecord> findBySend(boolean isSend);

    class SpecificationBuilder {

        public static Specification<VHelpRecord> buildBackendList(Long helpUserScid,Integer status,String keyword,String beginTime,String endTime){
            return (Specification<VHelpRecord>) (root, query, cb) -> {
                List<Predicate> list = new ArrayList<Predicate>();
                if (helpUserScid != null && helpUserScid != 0) {
                    list.add(cb.equal(root.get("helperScid").as(Integer.class), helpUserScid));
                }
                if (status != null && status != 0) {
                    //列表查询未处理
                    if (status == 1) {
                        list.add(cb.or(cb.equal(root.get("status").as(Integer.class), 0), cb.equal(root.get("status").as(Integer.class), 1), cb.equal(root.get("status").as(Integer.class), 2)));
                    } else {
                        list.add(cb.equal(root.get("status").as(Integer.class), status));
                    }
                }
                if (StrUtil.isNotBlank(keyword)) {
                    list.add(cb.or(
                            cb.like(root.get("docTitle").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")
                            )
                    );
                }
                if (StrUtil.isNotBlank(beginTime)) {
                    list.add(cb.between(root.get("gmtCreate").as(Date.class), DateUtil.parse(beginTime), DateUtil.parse(endTime)));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            };
        }


        public static Specification<VHelpRecord> buildVhelpRecord(List<Integer> channel, List<Integer> status, String email, Long helperId, String keyword) {
            return new Specification<VHelpRecord>() {
                @Override
                public Predicate toPredicate(Root<VHelpRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    if (helperId != null) {
                        list.add(cb.equal(root.get("helperId"), helperId));
                    }
                    if (StrUtil.isNotBlank(email)) {
                        list.add(cb.equal(root.get("helperEmail"), email));
                    }
                    // 渠道过滤
                    if (channel != null && channel.size() > 0) {
                        CriteriaBuilder.In<Integer> inChannel = cb.in(root.get("helpChannel"));
                        channel.forEach(inChannel::value);
                        list.add(inChannel);
                    }
                    // 状态过滤
                    if (status != null && status.size() > 0) {
                        CriteriaBuilder.In<Integer> inStatus = cb.in(root.get("status"));
                        status.forEach(inStatus::value);
                        list.add(inStatus);
                    }
                    if (StrUtil.isNotBlank(keyword)) {
                        list.add(cb.or(cb.like(root.get("docTitle").as(String.class), "%" + keyword.trim() + "%"), cb.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")));
                    }
                    Predicate[] p = new Predicate[list.size()];
                    return cb.and(list.toArray(p));
                }
            };
        }
    }

}

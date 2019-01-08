package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.VHelpRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VHelpRecordRepository extends JpaRepository<VHelpRecord, Long>, JpaSpecificationExecutor<VHelpRecord> {



    Page<VHelpRecord> findByHelperEmail(String helperEmail, Pageable pageable);

    Page<VHelpRecord> findByHelperIdAndStatus(long helperId, Integer status, Pageable pageable);
    Page<VHelpRecord> findByHelperId(long helperId, Pageable pageable);


    Page<VHelpRecord> findByStatusIn(int[] status, Pageable pageable);

    Page<VHelpRecord> findByHelpChannelAndStatusIn(int helpChannel, int[] status, Pageable pageable);


}

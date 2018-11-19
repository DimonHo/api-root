package com.wd.cloud.searchserver.repository;

import com.wd.cloud.searchserver.entity.DownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface DownloadInfoRepostory extends JpaRepository<DownloadInfo,Long> {

    @Query(value = "select  count(*) as downloads  from t_download_info  where school = ?1 and time like ?2%",nativeQuery = true)
    int findBySchoolAndTimeLike (String school,String time);
}

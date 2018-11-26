package com.wd.cloud.searchserver.repository;

import com.wd.cloud.searchserver.entity.DownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;


public interface DownloadInfoRepostory extends JpaRepository<DownloadInfo, Long> {

    @Query(value = "select  count(*) as downloads  from t_download_info  where school = ?1 and date_format(time,\"%Y-%m-%d %H:%i\") = date_format(?2,\"%Y-%m-%d %H:%i\")", nativeQuery = true)
    int findBySchoolAndTimeLike(String school, Date time);
}

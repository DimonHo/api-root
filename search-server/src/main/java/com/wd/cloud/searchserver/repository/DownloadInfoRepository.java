package com.wd.cloud.searchserver.repository;

import com.wd.cloud.searchserver.entity.DownloadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;


public interface DownloadInfoRepository extends JpaRepository<DownloadInfo, Long> {

    @Query(value = "select school as orgName, count(*) as dcCount  from t_download_info  where school = ?1 and date_format(time,?3) = date_format(?2,?3)", nativeQuery = true)
    List<Map<String, Object>> findBySchoolDcCount(String school, String time, String format);

    @Query(value = "select school as orgName, count(*) as dcCount  from t_download_info  where date_format(time,?2) = date_format(?1,?2) group by school", nativeQuery = true)
    List<Map<String, Object>> findAllSchoolDcCount(String time, String format);

}

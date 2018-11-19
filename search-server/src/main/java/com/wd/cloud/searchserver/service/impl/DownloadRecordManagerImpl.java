package com.wd.cloud.searchserver.service.impl;

import com.wd.cloud.searchserver.repository.DownloadInfoRepostory;
import com.wd.cloud.searchserver.service.DownloadRecordManagerI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("downloadRecordManagerService")
public class DownloadRecordManagerImpl implements DownloadRecordManagerI {
    @Autowired
    DownloadInfoRepostory downloadInfoRepostory;

    @Override
    public int getAllCount(String school, String date) {
        String substring = date.substring(0,16);
        return downloadInfoRepostory.findBySchoolAndTimeLike(school, substring);
    }
}

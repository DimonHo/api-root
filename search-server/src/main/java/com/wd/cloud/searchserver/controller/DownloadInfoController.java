package com.wd.cloud.searchserver.controller;

import cn.hutool.core.date.DateUtil;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.searchserver.service.DownloadRecordManagerI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DownloadInfoController {
    @Autowired
    DownloadRecordManagerI downloadRecordManagerI;

    /**
     * 获取下载量
     *
     * @param school
     * @param date
     * @return
     */
    @RequestMapping("/downloadsCount")
    public ResponseModel downloadsCount(@RequestParam String school,
                                        @RequestParam String date) {

        int downloads = downloadRecordManagerI.getAllCount(school, DateUtil.parse(date));
        return ResponseModel.ok().setBody(downloads);
    }
}

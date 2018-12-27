package com.wd.cloud.docdelivery.service.impl;

import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.MailService;
import com.wd.cloud.docdelivery.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
@Service("processService")
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    MailService mailService;

    @Override
    public void third(Long helpRecordId, Long giverId, String giverName) {

    }

    @Override
    public void give(Long helpRecordId, Long giverId, String giverName, MultipartFile file) {

    }

    @Override
    public void notFound(Long helpRecordId, Long giverId, String giverName) {

    }
}

package com.wd.cloud.crsserver.service.impl;

import com.wd.cloud.crsserver.pojo.document.Oafind;
import com.wd.cloud.crsserver.repository.OafindRepository;
import com.wd.cloud.crsserver.service.OafindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:51
 * @Description:
 */
@Service("oafindService")
public class OafindServiceImpl implements OafindService {

    @Autowired
    OafindRepository oafindRepository;

    @Override
    public Page<Oafind>  baseSearch() {
        Pageable pageable = PageRequest.of(0,5);
        Page<Oafind> pages = oafindRepository.findByDocumentType("article",pageable);
        return pages;
    }
}

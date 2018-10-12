package com.wd.cloud.reportanalysis.service.impl;

import com.wd.cloud.reportanalysis.entity.School;
import com.wd.cloud.reportanalysis.repository.SchoolRepository;
import com.wd.cloud.reportanalysis.service.SchoolServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolService implements SchoolServiceI {

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public School findByScid(int scid) {
        return schoolRepository.findByScid(scid);
    }

}

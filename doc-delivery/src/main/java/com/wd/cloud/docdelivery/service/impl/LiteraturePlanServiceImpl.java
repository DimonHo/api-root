package com.wd.cloud.docdelivery.service.impl;

import com.wd.cloud.docdelivery.pojo.entity.LiteraturePlan;
import com.wd.cloud.docdelivery.repository.LiteraturePlanRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.LiteraturePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service("literaturePlanServiceImpl")
public class LiteraturePlanServiceImpl implements LiteraturePlanService {

    @Autowired
    LiteraturePlanRepository literaturePlanRepository;

    @Override
    public List<LiteraturePlan> findByDate() {
        return literaturePlanRepository.findByDate();
    }
}

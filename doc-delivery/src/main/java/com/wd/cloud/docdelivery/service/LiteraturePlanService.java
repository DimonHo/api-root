package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.pojo.entity.LiteraturePlan;

import java.util.Date;
import java.util.List;

public interface LiteraturePlanService {

    public List<LiteraturePlan> findByDate();

}

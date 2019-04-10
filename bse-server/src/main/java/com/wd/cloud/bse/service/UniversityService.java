package com.wd.cloud.bse.service;

import com.wd.cloud.bse.entity.school.University;

import java.util.List;

public interface UniversityService {

    public University findById(int id);

    public List<University> findAll();

}

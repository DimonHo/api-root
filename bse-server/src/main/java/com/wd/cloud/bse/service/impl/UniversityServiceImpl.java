package com.wd.cloud.bse.service.impl;

import com.wd.cloud.bse.entity.school.University;
import com.wd.cloud.bse.repository.school.UniversityRepository;
import com.wd.cloud.bse.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityServiceImpl implements UniversityService {

    @Autowired
    UniversityRepository universityRepository;

    @Override
    public University findById(int id) {
        return universityRepository.findById(id);
    }

    @Override
    public List<University> findAll() {
        return universityRepository.findAll();
    }


}

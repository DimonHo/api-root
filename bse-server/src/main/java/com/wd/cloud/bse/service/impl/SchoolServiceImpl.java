package com.wd.cloud.bse.service.impl;

import com.wd.cloud.bse.entity.school.School;
import com.wd.cloud.bse.repository.school.SchoolRepository;
import com.wd.cloud.bse.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl implements SchoolService {
	
	@Autowired
	SchoolRepository schoolRepository;
	
	public School findByName(String name) {
		return schoolRepository.findByName(name);
	}

}

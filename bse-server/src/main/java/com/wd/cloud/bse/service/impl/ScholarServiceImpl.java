package com.wd.cloud.bse.service.impl;

import com.wd.cloud.bse.entity.Scholar;
import com.wd.cloud.bse.repository.ScholarRepository;
import com.wd.cloud.bse.service.ScholarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScholarServiceImpl implements ScholarService {
	
	@Autowired
	ScholarRepository scholarRepository;
	
	public Scholar findById(Integer id) {
		return scholarRepository.findById(id);
	}

}

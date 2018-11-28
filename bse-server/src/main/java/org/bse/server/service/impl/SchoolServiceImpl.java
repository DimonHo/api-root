package org.bse.server.service.impl;

import org.bse.server.entity.School;
import org.bse.server.repository.SchoolRepository;
import org.bse.server.service.SchoolService;
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

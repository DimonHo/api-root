package org.bse.server.service.impl;

import org.bse.server.entity.Scholar;
import org.bse.server.repository.ScholarRepository;
import org.bse.server.service.ScholarService;
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

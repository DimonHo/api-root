package com.wd.cloud.bse.service;

import java.util.List;

import com.wd.cloud.bse.entity.school.University;

public interface UniversityService {
	
	public University findById(int id);
	
	public List<University> findAll();

}

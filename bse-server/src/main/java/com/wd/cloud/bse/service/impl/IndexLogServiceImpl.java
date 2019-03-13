package com.wd.cloud.bse.service.impl;

import com.wd.cloud.bse.entity.xk.IndexLog;
import com.wd.cloud.bse.repository.xk.IndexLogRepository;
import com.wd.cloud.bse.service.IndexLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexLogServiceImpl implements IndexLogService {
	
	@Autowired
	IndexLogRepository indexLogRepository;
	
	@Override
    public void save(IndexLog indexLog) {
		indexLogRepository.save(indexLog);
	}
	

}

package com.wd.cloud.orgserver.service.impl;

import com.wd.cloud.orgserver.entity.IpRange;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.service.IpSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ipService")
public class IpSettingServiceImpl implements IpSettingService {

    @Autowired
    IpRangeRepository repository;
    @Override
    public List<IpRange> getAll() {
        List<IpRange> all = repository.findAll();
        return all;
    }

    @Override
    public IpRange findByBeginAndEnd(String begin, String end) {
        return repository.findByBeginAndEnd(begin,end);
    }
}

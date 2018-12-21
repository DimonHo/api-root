package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.IpRange;

import java.util.List;

public interface IpSettingService {
    List<IpRange> getAll();

    IpRange findByBeginAndEnd(String begin,String end);
}

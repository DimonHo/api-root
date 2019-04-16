package com.wd.cloud.uoserver.service.impl;

import com.wd.cloud.uoserver.repository.OrgRepository;
import com.wd.cloud.uoserver.repository.UserRepository;
import com.wd.cloud.uoserver.service.TjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/20 18:20
 * @Description:
 */
@Service("tjService")
public class TjServiceImpl implements TjService {

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<Map<String, Object>> tjOrgUser() {
        List<Map<String, Object>> orgUserMap = userRepository.tjOrgUser();

        return orgUserMap;
    }

    @Override
    public List<Map<String, Object>> tjOrgDeptUser(String orgFlag) {
        return userRepository.tjOrgDepartUser(orgFlag);
    }
}

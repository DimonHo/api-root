package com.wd.cloud.docdelivery.service.impl;

import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.TempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/1/2
 * @Description:
 */
@Slf4j
@Service("tempService")
public class TempServiceImpl implements TempService {

    @Autowired
    LiteratureRepository literatureRepository;


    @Override
    public int updateLiteratureUnid() {
        List<Literature> literatures = literatureRepository.findByUnidIsNull();
        for (Literature literature : literatures) {
            literature.updateUnid();
            try {
                literatureRepository.save(literature);
            } catch (Exception e) {
                log.error("唯一主键冲突:[{}]", literature.getUnid());
            }

        }
        return literatures.size();
    }
}

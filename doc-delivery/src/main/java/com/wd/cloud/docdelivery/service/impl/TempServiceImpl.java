package com.wd.cloud.docdelivery.service.impl;

import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/1/2
 * @Description:
 */
@Service("tempService")
public class TempServiceImpl implements TempService {

    @Autowired
    LiteratureRepository literatureRepository;


    @Override
    public int updateLiteratureUnid() {
        List<Literature> literatures = literatureRepository.findByUnidIsNull();
        List<Literature> newLiteratures = new ArrayList<>();
        for (Literature literature : literatures) {
            literature.updateUnid();
            newLiteratures.add(literature);
            if (newLiteratures.size() % 1000 == 0) {
                literatureRepository.saveAll(newLiteratures);
                newLiteratures = new ArrayList<>();
            }
        }
        literatureRepository.saveAll(newLiteratures);
        return literatures.size();
    }
}

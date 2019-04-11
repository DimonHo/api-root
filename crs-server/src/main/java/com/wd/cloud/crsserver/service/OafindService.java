package com.wd.cloud.crsserver.service;

import com.wd.cloud.crsserver.pojo.document.Oafind;
import org.springframework.data.domain.Page;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:51
 * @Description:
 */
public interface OafindService {

    Page<Oafind> baseSearch();
}

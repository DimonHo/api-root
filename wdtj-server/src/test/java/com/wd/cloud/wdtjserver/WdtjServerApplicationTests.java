package com.wd.cloud.wdtjserver;

import cn.hutool.core.date.DateUtil;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.service.TjService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WdtjServerApplicationTests {

    @Autowired
    TjService tjService;

    @Test
    public void contextLoads() {

    }

}

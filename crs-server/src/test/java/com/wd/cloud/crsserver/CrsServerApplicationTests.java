package com.wd.cloud.crsserver;

import com.wd.cloud.crsserver.service.OafindService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrsServerApplicationTests {

    @Autowired
    OafindService oafindService;

    @Test
    public void contextLoads() {
        oafindService.baseSearch();
    }

}

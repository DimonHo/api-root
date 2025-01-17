package com.wd.cloud.wdtjserver;

import com.wd.cloud.wdtjserver.task.AutoTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class WdtjServerApplicationTests {
    @Autowired
    AutoTask autoTask;

    @Test
    public void contextLoads() {
        autoTask.mergeData();
    }


}

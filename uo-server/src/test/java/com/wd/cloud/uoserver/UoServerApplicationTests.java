package com.wd.cloud.uoserver;

import cn.hutool.core.lang.Console;
import com.wd.cloud.uoserver.pojo.entity.OrgProd;
import com.wd.cloud.uoserver.repository.OrgProdRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UoServerApplicationTests {

    OrgProdRepository orgProdRepository;

    @Test
    public void contextLoads() {

        OrgProd product = orgProdRepository.findByOrgFlagAndProdId("wdkj", null).orElse(new OrgProd());
        Console.log(product.toString());
    }

}

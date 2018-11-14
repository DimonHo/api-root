package com.wd.cloud.wdtjserver;

import cn.hutool.core.date.DateUtil;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
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
        TjHisSetting tjHisSetting = new TjHisSetting();
        tjHisSetting.setOrgId(1L)
                .setBeginTime(DateUtil.parseDateTime("2018-10-01 10:02:00").toTimestamp())
                .setEndTime(DateUtil.parseDateTime("2018-10-01 11:06:00").toTimestamp())
                .setPvCount(523)
                .setScCount(244)
                .setDcCount(124)
                .setDdcCount(165)
                .setAvgTime(new Time(520000));
        tjService.buildTjHisData(tjHisSetting);
    }

}

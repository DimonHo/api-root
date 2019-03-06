package com.wd.cloud.uoserver;

import cn.hutool.core.lang.Console;
import com.wd.cloud.commons.util.NetUtil;
import org.junit.Test;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public class JunitTest {

    @Test
    public void test1() {
        Long i = NetUtil.ipToLong("113.240.226.337");
        Long j = cn.hutool.core.util.NetUtil.ipv4ToLong("113.240.226.337");
        String ip1 = NetUtil.longToIp(i);
        String ip2 = cn.hutool.core.util.NetUtil.longToIpv4(i);

        Console.log("i={},j={},ip1={},ip2={}", i, j, ip1, ip2);

    }
}

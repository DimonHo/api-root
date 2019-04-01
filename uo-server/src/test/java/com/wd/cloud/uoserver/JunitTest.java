package com.wd.cloud.uoserver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Console;
import com.wd.cloud.commons.util.NetUtil;
import com.wd.cloud.uoserver.pojo.entity.User;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
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

    @Test
    public void test2(){
        PerfectUserVO perfectUserVO = new PerfectUserVO();
        perfectUserVO.setUsername("aaaa");
        perfectUserVO.setOrgFlag("wdkj");
        User user = new User();
        user.setNickname("nick");
        user.setUsername("bbbb");
        BeanUtil.copyProperties(perfectUserVO,user,"username");
        Console.log(user);
    }

    @Test
    public void test3(){
        Boolean isExp = null;
        int a = isExp?1:2;
        Console.log(a);
        
    }
}

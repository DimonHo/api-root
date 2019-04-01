package com.wd.cloud.docdelivery;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import org.junit.Test;

/**
 * @author He Zhigang
 * @date 2019/1/8
 * @Description:
 */
public class JunitTest {
    @Test
    public void test1(){
        String templateFile = "中国-success.ftl";
        templateFile.replace(StrUtil.subBefore(templateFile,"-",false),"default");
        Console.log(templateFile);
    }
}

package com.wd.cloud.fsserver;

import com.wd.cloud.fsserver.util.FileUtil;
import org.junit.Test;

import java.io.File;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description:
 */
public class JunitTest {

    @Test
    public void testMd5() {
        File file = new File("D:\\Downloads\\ideaIU-2018.2.4.exe");
        System.out.println("文件size:" + file.length());
        FileUtil.fileMd5(file);
    }
}

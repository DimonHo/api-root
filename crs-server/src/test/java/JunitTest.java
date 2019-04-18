import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import org.junit.Test;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/15 19:09
 * @Description:
 */
public class JunitTest {

    @Test
    public void test1(){
        String year = "-2015";
        Integer start = Integer.valueOf(year.split("-")[0]);
        Integer end = Integer.valueOf(year.split("-")[1]);
        Console.log("{}-{}",start,end);

    }
}

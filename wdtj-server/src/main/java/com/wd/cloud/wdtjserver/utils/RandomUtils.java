package com.wd.cloud.wdtjserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public class RandomUtils {

    public static List<Integer> random(int num, int total) {
        List<Integer> list = new ArrayList<Integer>();
        int rand;
        long all = 0;
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            rand = random.nextInt(total);
            list.add(rand);
            all += rand;
        }

        List<Integer> result = new ArrayList<Integer>();
        Double data;
        for (int i = 0; i < num; i++) {
            data = list.get(i) * 1.0 * total / all;
            result.add(data.intValue());
        }
        return result;
    }

}

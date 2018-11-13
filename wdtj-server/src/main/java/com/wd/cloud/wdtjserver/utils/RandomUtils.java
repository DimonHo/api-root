package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

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
        int sum = 0;
        for (int j : result) {
            sum += j;
        }
        Console.log("总和：{}", sum);
        return result;
    }

    /**
     * 根据总量和权重生成数据
     *
     * @param dateTimeFloatMap
     * @param total
     * @return
     */
    public static Map<DateTime, Integer> random(Map<DateTime, Float> dateTimeFloatMap, int total) {
        Map<DateTime, Integer> randomResult = new TreeMap<>();
        List<Integer> randomList = new ArrayList<>();
        randomList.add(0);
        dateTimeFloatMap.forEach((k, v) -> {
            randomList.add(RandomUtil.randomInt(total));
        });
        randomList.remove(randomList.size() - 1);
        randomList.add(total);
        List<Integer> randomListSort = randomList.stream().sorted().collect(Collectors.toList());
        List<Integer> newRandomList = new ArrayList<>();
        for (int i = 1; i < randomListSort.size(); i++) {
            int e = randomListSort.get(i) - randomListSort.get(i - 1);
            newRandomList.add(e);
        }
        Console.log(newRandomList);
        return null;
    }

}

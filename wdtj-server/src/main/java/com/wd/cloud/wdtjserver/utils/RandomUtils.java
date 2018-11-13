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

    public static List<Integer> random2(int num, int total,List<Double> weights,double waveRate) {
        List<Integer> list = new ArrayList<Integer>();
        int rand;
        long all=0;
        Random random = new Random();
        double rate;
        for (int i = 0; i < num; i++) {
            rate = random.nextGaussian()*weights.get(i)*total*waveRate;
            rand = (int)(rate+Math.ceil(weights.get(i)*total));
            list.add(rand);
            all+=rand;
        }

        List<Integer> result = new ArrayList<Integer>();
        Double data;
        long sum = 0;
        for (int i = 0; i < num; i++) {
            data = list.get(i)*1.0*total/all;
            rand = (int)Math.ceil(data);
            result.add(rand);
            sum+=rand;
        }
//		List<Integer> result = new ArrayList<Integer>(Arrays.asList(12, 20, 31, 11, 21, 10));
        System.out.println(sum);
        System.out.println(result);
        if (sum!=total) {
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            for (int i = 0; i < num; i++) {
                rate = weights.get(i)*total-result.get(i);
                map.put(i, rate);
            }
            List<Map.Entry<Integer, Double>> enList = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
            Collections.sort(enList, new Comparator<Map.Entry<Integer, Double>>() {
                public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            long diff = sum - total;
            if (diff<0) {
                int j = enList.size()-1;
                while (diff<0) {
                    if (j<0) {
                        j = enList.size()-1;
                    }
                    if (enList.get(j).getValue()>0) {
                        rand = result.get(enList.get(j).getKey());
                        result.set(enList.get(j).getKey(), rand-1);
                        j--;
                        diff++;
                    }
                    else {
                        j = enList.size()-1;
                    }
                }
            }
            else {
                int j = 0;
                while (diff>0) {
                    if (j == enList.size()-1) {
                        j = 0;
                    }
                    if (enList.get(j).getValue()<0) {
                        rand = result.get(enList.get(j).getKey());
                        result.set(enList.get(j).getKey(), rand-1);
                        j++;
                        diff--;
                    }
                    else {
                        j = 0;
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(random(60,100));
        for (int i = 0; i < 1000; i++) {
            System.out.println(random2(6,8000, Arrays.asList(0.1,0.2,0.3,0.1,0.2,0.1),0.2));
        }
    }

}

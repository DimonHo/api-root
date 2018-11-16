package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.lang.Console;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.WeightModel;

import java.sql.Time;
import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public class RandomUtil extends cn.hutool.core.util.RandomUtil {

    /**
     * 生成总和为固定值的随机数列表
     *
     * @param total 随机数之和
     * @param count 随机数的个数
     * @return
     */
    public static List<Long> randomListFromFinalTotal(long total, int count) {
        List<Long> tempList = new ArrayList<>();
        //首尾添加0和total
        tempList.add(0L);
        tempList.add(total);
        if (total > 0) {
            //生成 count-1 个随机数
            for (int i = 1; i < count; i++) {
                tempList.add(RandomUtil.randomLong(total));
            }
        } else {
            for (int i = 1; i < count; i++) {
                tempList.add(RandomUtil.randomLong(total, 0));
            }
        }
        //将随机数排序
        Collections.sort(tempList);
        if (total < 0) {
            // 如果是负数，将列表反转
            Collections.reverse(tempList);
        }
        List<Long> randomList = new ArrayList<>();
        // 将数组相邻元素之差组装成最终结果
        for (int i = 1; i < tempList.size(); i++) {
            randomList.add(tempList.get(i) - tempList.get(i - 1));
        }
        return randomList;
    }

    /**
     * 生成总和为固定值的随机数列表
     *
     * @param total 随机数之和
     * @param count 随机数的个数
     * @return
     */
    public static List<Integer> randomListFromFinalTotal(int total, int count) {
        List<Integer> tempList = new ArrayList<>();
        //首尾添加0和total
        tempList.add(0);
        tempList.add(total);
        if (total > 0) {
            //生成 count-1 个随机数
            for (int i = 1; i < count; i++) {
                tempList.add(RandomUtil.randomInt(total));
            }
        } else {
            for (int i = 1; i < count; i++) {
                tempList.add(RandomUtil.randomInt(total, 0));
            }
        }
        //将随机数排序
        Collections.sort(tempList);
        if (total < 0) {
            // 如果是负数，将列表反转
            Collections.reverse(tempList);
        }
        List<Integer> randomList = new ArrayList<>();
        // 将数组相邻元素之差组装成最终结果
        for (int i = 1; i < tempList.size(); i++) {
            randomList.add(tempList.get(i) - tempList.get(i - 1));
        }
        return randomList;
    }


    /**
     * 根据历史记录随机生成历史详细数据
     *
     * @param weightMap
     * @param ratio     上下浮动比例[0.1-0.9]
     * @return
     */
    public static List<TjViewData> buildHisDataFromWeight(TjHisQuota tjHisQuota, Map<WeightModel, TjViewData> weightMap, double ratio) {
        //权重总和
        double weightSum = weightMap.keySet().stream().map(a -> a.getValue()).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();
        Console.log("平均权重：{}", avgWeight);

        // 生成随机访问时长
        long avgTimeTotal = tjHisQuota.getAvgTime().getTime() * weightMap.size();
        List<Long> avgTimeRandomList = randomListFromFinalTotal(avgTimeTotal, weightMap.size());
        Collections.sort(avgTimeRandomList);
        // 将访问时长倒序排列
        Collections.reverse(avgTimeRandomList);

        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        // 计算平均值
        double pvAvg = 1.0 * pvTotal / weightMap.size() / avgWeight;
        double scAvg = 1.0 * scTotal / weightMap.size() / avgWeight;
        double dcAvg = 1.0 * dcTotal / weightMap.size() / avgWeight;
        double ddcAvg = 1.0 * ddcTotal / weightMap.size() / avgWeight;

        while (pvTotal > 0 || scTotal > 0 || dcTotal > 0 || ddcTotal > 0) {
            int avtTimeRandomListIndex = 0;
            for (Map.Entry<WeightModel, TjViewData> entry : weightMap.entrySet()) {
                // 权重
                double weight = entry.getKey().getValue();
                //随机PV量
                int randomPv = getRandomIntFromAvg(pvAvg, weight, ratio);
                int pvCount = randomPv > pvTotal ? pvTotal : randomPv;

                // 随机搜索量必须小于PV量
                int randomSc = getRandomIntFromAvg(scAvg, weight, ratio);
                randomSc = pvCount == 0 ? 0 : randomSc;
                int scCount = randomSc > scTotal ? scTotal : randomSc;

                //随机下载量，如果当前PV量为0，则下载量等于0
                int randomDc = getRandomIntFromAvg(dcAvg, weight, ratio);
                randomDc = pvCount == 0 ? 0 : randomDc;
                int dcCount = randomDc > dcTotal ? dcTotal : randomDc;

                //随机文献传递量，如果当前PV量为0，则文献传递量等于0
                int randomDdc = getRandomIntFromAvg(ddcAvg, weight, ratio);
                randomDdc = pvCount == 0 ? 0 : randomDdc;
                int ddcCount = randomDdc > ddcTotal ? ddcTotal : randomDdc;

                entry.getValue().setPvCount(entry.getValue().getPvCount() + pvCount);
                pvTotal -= pvCount;

                entry.getValue().setScCount(entry.getValue().getScCount() + scCount);
                scTotal -= scCount;

                entry.getValue().setDcCount(entry.getValue().getDcCount() + dcCount);
                dcTotal -= dcCount;

                entry.getValue().setDdcCount(entry.getValue().getDdcCount() + ddcCount);
                ddcTotal -= ddcCount;

                entry.getValue().setAvgTime(new Time(avgTimeRandomList.get(avtTimeRandomListIndex++)));
            }
        }
        return new ArrayList(weightMap.values());
    }


    /**
     * 根据历史记录随机生成历史详细数据
     *
     * @param weightMap
     * @param ratio     上下浮动比例[0.1-0.9]
     * @return
     */
    public static List<TjTaskData> buildDayDataFromWeight(TjQuota tjQuota, Map<WeightModel, TjTaskData> weightMap, double ratio) {
        //权重总和
        double weightSum = weightMap.keySet().stream().map(a -> a.getValue()).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();
        Console.log("平均权重：{}", avgWeight);

        // 生成随机访问时长
        long avgTimeTotal = tjQuota.getAvgTime().getTime() * weightMap.size();
        List<Long> avgTimeRandomList = randomListFromFinalTotal(avgTimeTotal, weightMap.size());
        Collections.sort(avgTimeRandomList);
        // 将访问时长倒序排列
        Collections.reverse(avgTimeRandomList);

        int pvTotal = tjQuota.getPvCount();
        int scTotal = tjQuota.getScCount();
        int dcTotal = tjQuota.getDcCount();
        int ddcTotal = tjQuota.getDdcCount();
        // 计算平均值 总量除以分钟数再除以平均权重
        double pvAvg = 1.0 * pvTotal / weightMap.size() / avgWeight;
        double scAvg = 1.0 * scTotal / weightMap.size() / avgWeight;
        double dcAvg = 1.0 * dcTotal / weightMap.size() / avgWeight;
        double ddcAvg = 1.0 * ddcTotal / weightMap.size() / avgWeight;
        int avtTimeRandomListIndex = 0;
        for (Map.Entry<WeightModel, TjTaskData> entry : weightMap.entrySet()) {
            // 权重
            double weight = entry.getKey().getValue();
            //随机PV量
            int randomPv = getRandomIntFromAvg(pvAvg, weight, ratio);

            // 随机搜索量必须小于PV量
            int randomSc = getRandomIntFromAvg(scAvg, weight, ratio);
            randomSc = randomPv == 0 ? 0 : randomSc;

            //随机下载量，如果当前PV量为0，则下载量等于0
            int randomDc = getRandomIntFromAvg(dcAvg, weight, ratio);
            randomDc = randomPv == 0 ? 0 : randomDc;

            //随机文献传递量，如果当前PV量为0，则文献传递量等于0
            int randomDdc = getRandomIntFromAvg(ddcAvg, weight, ratio);
            randomDdc = randomPv == 0 ? 0 : randomDdc;

            entry.getValue().setPvCount(entry.getValue().getPvCount() + randomPv);

            entry.getValue().setScCount(entry.getValue().getScCount() + randomSc);

            entry.getValue().setDcCount(entry.getValue().getDcCount() + randomDc);

            entry.getValue().setDdcCount(entry.getValue().getDdcCount() + randomDdc);

            entry.getValue().setAvgTime(new Time(avgTimeRandomList.get(avtTimeRandomListIndex++)));
        }
        return new ArrayList(weightMap.values());
    }

    /**
     * @param ratio  浮动比例
     * @param pvAvg  平均值
     * @param weight 权重值
     * @return
     */
    private static int getRandomIntFromAvg(double pvAvg, double weight, double ratio) {
        double min = pvAvg * weight * (1 - ratio);
        double max = pvAvg * weight * (1 + ratio);
        //如果最大值不大于1，最大值设为1
        return (int) Math.round(RandomUtil.randomDouble(min, max > 1 ? max : 1));
    }


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


    public static List<Integer> random2(int num, int total, List<Double> weights, double waveRate) {
        List<Integer> list = new ArrayList<Integer>();
        int rand;
        long all = 0;
        Random random = new Random();
        double rate;
        for (int i = 0; i < num; i++) {
            rate = random.nextGaussian() * weights.get(i) * total * waveRate;
            rand = (int) (rate + Math.ceil(weights.get(i) * total));
            list.add(rand);
            all += rand;
        }

        List<Integer> result = new ArrayList<Integer>();
        Double data;
        long sum = 0;
        for (int i = 0; i < num; i++) {
            data = list.get(i) * 1.0 * total / all;
            rand = (int) Math.ceil(data);
            result.add(rand);
            sum += rand;
        }
//		List<Integer> result = new ArrayList<Integer>(Arrays.asList(12, 20, 31, 11, 21, 10));
        System.out.println(sum);
        System.out.println(result);
        if (sum != total) {
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            for (int i = 0; i < num; i++) {
                rate = weights.get(i) * total - result.get(i);
                map.put(i, rate);
            }
            List<Map.Entry<Integer, Double>> enList = new ArrayList<Map.Entry<Integer, Double>>(map.entrySet());
            Collections.sort(enList, new Comparator<Map.Entry<Integer, Double>>() {
                public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            long diff = sum - total;
            if (diff < 0) {
                int j = enList.size() - 1;
                while (diff < 0) {
                    if (j < 0) {
                        j = enList.size() - 1;
                    }
                    if (enList.get(j).getValue() > 0) {
                        rand = result.get(enList.get(j).getKey());
                        result.set(enList.get(j).getKey(), rand - 1);
                        j--;
                        diff++;
                    } else {
                        j = enList.size() - 1;
                    }
                }
            } else {
                int j = 0;
                while (diff > 0) {
                    if (j == enList.size() - 1) {
                        j = 0;
                    }
                    if (enList.get(j).getValue() < 0) {
                        rand = result.get(enList.get(j).getKey());
                        result.set(enList.get(j).getKey(), rand - 1);
                        j++;
                        diff--;
                    } else {
                        j = 0;
                    }
                }
            }
        }
        return result;
    }

//    public static void main(String[] args) {
//        System.out.println(random(60,100));
//        for (int i = 0; i < 1000; i++) {
//            System.out.println(random2(6,8000, Arrays.asList(0.1,0.2,0.3,0.1,0.2,0.1),0.2));
//        }
//    }

}

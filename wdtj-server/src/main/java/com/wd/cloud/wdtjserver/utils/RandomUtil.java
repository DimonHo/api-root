package com.wd.cloud.wdtjserver.utils;

import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.WeightModel;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        double weightSum = weightMap.keySet().stream().map(WeightModel::getValue).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();
        // 生成随机访问时长
        long avgTimeTotal = tjHisQuota.getAvgTime().getTime() * weightMap.size();
        List<Long> avgTimeRandomList = randomListFromFinalTotal(avgTimeTotal, weightMap.size());

        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        // 计算平均值
        double pvAvg = 1.0 * pvTotal / weightMap.size() / avgWeight;
        double scAvg = 1.0 * scTotal / weightMap.size() / avgWeight;
        double dcAvg = 1.0 * dcTotal / weightMap.size() / avgWeight;
        double ddcAvg = 1.0 * ddcTotal / weightMap.size() / avgWeight;

        while (pvTotal > 0 || scTotal > 0 || dcTotal > 0 || ddcTotal > 0 || avgTimeRandomList.size() > 0) {
            for (Map.Entry<WeightModel, TjViewData> entry : weightMap.entrySet()) {
                // 权重
                double weight = entry.getKey().getValue();
                //随机PV量
                int randomPv = getRandomIntFromAvg(pvAvg, weight, ratio);
                int pvCount = randomPv > pvTotal ? pvTotal : randomPv;
                entry.getValue().setPvCount(entry.getValue().getPvCount() + pvCount);
                pvTotal -= pvCount;

                if (entry.getValue().getPvCount() == 0) {
                    entry.getValue().setScCount(0);
                    entry.getValue().setDcCount(0);
                    entry.getValue().setDdcCount(0);
                    entry.getValue().setVisitTime(new Time(0));
                } else {
                    // 随机搜索量必须小于PV量
                    int randomSc = getRandomIntFromAvg(scAvg, weight, ratio);
                    int scCount = randomSc > scTotal ? scTotal : randomSc;
                    if (entry.getValue().getScCount() + scCount > entry.getValue().getPvCount()) {
                        scCount = entry.getValue().getPvCount() - entry.getValue().getScCount();
                    }
                    entry.getValue().setScCount(entry.getValue().getScCount() + scCount);
                    scTotal -= scCount;

                    //随机下载量，如果当前PV量为0，则下载量等于0
                    int randomDc = getRandomIntFromAvg(dcAvg, weight, ratio);
                    int dcCount = randomDc > dcTotal ? dcTotal : randomDc;
                    entry.getValue().setDcCount(entry.getValue().getDcCount() + dcCount);
                    dcTotal -= dcCount;

                    //随机文献传递量，如果当前PV量为0，则文献传递量等于0
                    int randomDdc = getRandomIntFromAvg(ddcAvg, weight, ratio);
                    int ddcCount = randomDdc > ddcTotal ? ddcTotal : randomDdc;
                    entry.getValue().setDdcCount(entry.getValue().getDdcCount() + ddcCount);
                    ddcTotal -= ddcCount;

                    if (avgTimeRandomList.size() > 0) {
                        entry.getValue().setVisitTime(new Time(entry.getValue().getVisitTime().getTime() + randomEle(avgTimeRandomList, true)));
                    }

                }
            }
        }
        return new ArrayList<>(weightMap.values());
    }

    /**
     * 从列表中随机取出一个元素，并删除该元素
     *
     * @param list
     * @param remove
     * @param <T>
     * @return
     */
    public static <T> T randomEle(List<T> list, boolean remove) {
        int index = randomInt(list.size());
        T e = list.get(index);
        if (remove) {
            list.remove(index);
        }
        return e;
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
        double weightSum = weightMap.keySet().stream().map(WeightModel::getValue).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();
        int pvTotal = tjQuota.getPvCount();
        int scTotal = tjQuota.getScCount();
        int dcTotal = tjQuota.getDcCount();
        int ddcTotal = tjQuota.getDdcCount();
        // 计算平均值 总量除以分钟数再除以平均权重
        double pvAvg = 1.0 * pvTotal / weightMap.size() / avgWeight;
        double scAvg = 1.0 * scTotal / weightMap.size() / avgWeight;
        double dcAvg = 1.0 * dcTotal / weightMap.size() / avgWeight;
        double ddcAvg = 1.0 * ddcTotal / weightMap.size() / avgWeight;
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

            Time randomVisitTime = getRandomVisitTime(tjQuota.getAvgTime(), weight, ratio);
            randomVisitTime = randomPv == 0 ? new Time(0) : randomVisitTime;

            entry.getValue().setPvCount(randomPv);

            entry.getValue().setScCount(randomSc);

            entry.getValue().setDcCount(randomDc);

            entry.getValue().setDdcCount(randomDdc);

            entry.getValue().setVisitTime(randomVisitTime);

            entry.getValue().setId(new TjDataPk(tjQuota.getOrgId(), DateUtil.parse(entry.getKey().getName())));
        }
        return new ArrayList<>(weightMap.values());
    }

    /**
     * @param ratio  浮动比例
     * @param avg    平均值
     * @param weight 权重值
     * @return
     */
    private static int getRandomIntFromAvg(double avg, double weight, double ratio) {
        double min = avg * weight * (1 - ratio);
        double max = avg * weight * (1 + ratio);
        //如果最大值不大于1，最大值设为1
        return (int) Math.round(RandomUtil.randomDouble(min, max > 1 ? max : 1));
    }

    /**
     * 获取访问时长
     *
     * @param avgTime
     * @param weight
     * @param ratio
     * @return
     */
    public static Time getRandomVisitTime(Time avgTime, double weight, double ratio) {
        double min = avgTime.getTime() * weight * (1 - ratio);
        double max = avgTime.getTime() * weight * (1 + ratio);
        //如果最大值不大于1，最大值设为1
        return new Time(Math.round(RandomUtil.randomDouble(min, max > 1 ? max : 1)));
    }

}

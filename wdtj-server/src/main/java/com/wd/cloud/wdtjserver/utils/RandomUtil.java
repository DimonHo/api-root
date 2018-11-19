package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.model.WeightModel;

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
        if (total == 0) {
            return tempList;
        }
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
        if (total == 0) {
            return tempList;
        }
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
    public static List<HourTotalModel> buildHisDataFromWeight(TjHisQuota tjHisQuota, Map<WeightModel, HourTotalModel> weightMap, double ratio) {

        //权重总和
        double weightSum = weightMap.keySet().stream().map(WeightModel::getValue).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();


        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        int uvTotal = tjHisQuota.getUvCount();
        int ucTotal = tjHisQuota.getUcCount();

        // 访问总时长
        long avgTimeTotal = tjHisQuota.getAvgTime() * ucTotal;
        List<Long> avgTimeRandomList = randomListFromFinalTotal(avgTimeTotal, ucTotal);

        // 计算平均值
        double pvAvg = getAvg(pvTotal, weightMap.size(), avgWeight);
        double scAvg = getAvg(scTotal, weightMap.size(), avgWeight);
        double dcAvg = getAvg(dcTotal, weightMap.size(), avgWeight);
        double ddcAvg = getAvg(ddcTotal, weightMap.size(), avgWeight);
        double uvAvg = getAvg(uvTotal, weightMap.size(), avgWeight);
        double ucAvg = getAvg(ucTotal, weightMap.size(), avgWeight);

        while (pvTotal > 0 || scTotal > 0 || dcTotal > 0 || ddcTotal > 0 || avgTimeRandomList.size() > 0) {
            for (Map.Entry<WeightModel, HourTotalModel> entry : weightMap.entrySet()) {
                // 权重
                double weight = entry.getKey().getValue();
                //随机PV量
                int randomPv = getRandomIntFromAvg(pvAvg, weight, ratio);
                int pvCount = randomPv > pvTotal ? pvTotal : randomPv;
                entry.getValue().setPvTotal(entry.getValue().getPvTotal() + pvCount);
                pvTotal -= pvCount;

                if (entry.getValue().getPvTotal() == 0) {
                    entry.getValue().setScTotal(0)
                            .setDcTotal(0)
                            .setDdcTotal(0)
                            .setVisitTimeTotal(0)
                            .setUvTotal(0)
                            .setUcTotal(0);
                } else {
                    // 随机搜索量必须小于PV量
                    int randomSc = getRandomIntFromAvg(scAvg, weight, ratio);
                    int scCount = randomSc > scTotal ? scTotal : randomSc;
                    if (entry.getValue().getScTotal() + scCount > entry.getValue().getPvTotal()) {
                        scCount = entry.getValue().getPvTotal() - entry.getValue().getScTotal();
                    }
                    entry.getValue().setScTotal(entry.getValue().getScTotal() + scCount);
                    scTotal -= scCount;

                    //随机下载量，如果当前PV量为0，则下载量等于0
                    int randomDc = getRandomIntFromAvg(dcAvg, weight, ratio);
                    int dcCount = randomDc > dcTotal ? dcTotal : randomDc;
                    entry.getValue().setDcTotal(entry.getValue().getDcTotal() + dcCount);
                    dcTotal -= dcCount;

                    //随机文献传递量，如果当前PV量为0，则文献传递量等于0
                    int randomDdc = getRandomIntFromAvg(ddcAvg, weight, ratio);
                    int ddcCount = randomDdc > ddcTotal ? ddcTotal : randomDdc;
                    entry.getValue().setDdcTotal(entry.getValue().getDdcTotal() + ddcCount);
                    ddcTotal -= ddcCount;

                    if (avgTimeRandomList.size() > 0) {
                        entry.getValue().setVisitTimeTotal(entry.getValue().getVisitTimeTotal() + randomEle(avgTimeRandomList, true));
                    }

                }
            }
        }
        return new ArrayList<>(weightMap.values());
    }

    /**
     * 加权计算平均值
     *
     * @param total     总数
     * @param size      个数
     * @param avgWeight 权重
     * @return
     */
    private static double getAvg(int total, int size, double avgWeight) {
        return 1.0 * total / size / avgWeight;
    }


    /**
     * 根据日基数随机生成每天详细数据
     *
     * @param weightMap
     * @param ratio     上下浮动比例[0.1-0.9]
     * @return
     */
    public static List<HourTotalModel> buildDayDataFromWeight(TjQuota tjQuota, Map<WeightModel, HourTotalModel> weightMap, double ratio) {
        //权重总和
        double weightSum = weightMap.keySet().stream().map(WeightModel::getValue).reduce((a, b) -> a + b).orElse(weightMap.size() * 1.0);
        // 平均权重
        double avgWeight = weightSum / weightMap.size();
        int pvTotal = tjQuota.getPvCount();
        int scTotal = tjQuota.getScCount();
        int dcTotal = tjQuota.getDcCount();
        int ddcTotal = tjQuota.getDdcCount();
        int uvTotal = tjQuota.getUvCount();
        int ucTotal = tjQuota.getUcCount();

        // 计算平均值 总量除以分钟数再除以平均权重
        double pvAvg = getAvg(pvTotal, weightMap.size(), avgWeight);
        double scAvg = getAvg(scTotal, weightMap.size(), avgWeight);
        double dcAvg = getAvg(dcTotal, weightMap.size(), avgWeight);
        double ddcAvg = getAvg(ddcTotal, weightMap.size(), avgWeight);
        double uvAvg = getAvg(uvTotal, weightMap.size(), avgWeight);
        double ucAvg = getAvg(ucTotal, weightMap.size(), avgWeight);

        for (Map.Entry<WeightModel, HourTotalModel> entry : weightMap.entrySet()) {
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

            long randomVisitTime = getRandomVisitTime(tjQuota.getAvgTime(), weight, ratio);
            randomVisitTime = randomPv == 0 ? 0 : randomVisitTime;

            entry.getValue().setPvTotal(randomPv);

            entry.getValue().setScTotal(randomSc);

            entry.getValue().setDcTotal(randomDc);

            entry.getValue().setDdcTotal(randomDdc);

            entry.getValue().setVisitTimeTotal(randomVisitTime);

            entry.getValue().setOrgId(tjQuota.getOrgId());
        }
        return new ArrayList<>(weightMap.values());
    }

    /**
     * 随机
     *
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
    public static long getRandomVisitTime(long avgTime, double weight, double ratio) {
        double min = avgTime * weight * (1 - ratio);
        double max = avgTime * weight * (1 + ratio);
        //如果最大值不大于1，最大值设为1
        return Math.round(RandomUtil.randomDouble(min, max > 1 ? max : 1));
    }


    /**
     * 生成每分钟的随机数据
     *
     * @param hourTotalModel
     * @return
     */
    public static List<AbstractTjDataEntity> buildMinuteData(HourTotalModel hourTotalModel, Class clazz) {

        List<AbstractTjDataEntity> tjDataList = new ArrayList<>();
        //获取分钟列表
        List<DateTime> dates = DateUtil.rangeMinuteFromHours(hourTotalModel.getHourDate());

        int pvTotal = hourTotalModel.getPvTotal();
        int scTotal = hourTotalModel.getScTotal();
        int dcTotal = hourTotalModel.getDcTotal();
        int ddcTotal = hourTotalModel.getDdcTotal();
        int uvTotal = hourTotalModel.getUvTotal();
        int ucTotal = hourTotalModel.getUcTotal();
        long visitTimeTotal = hourTotalModel.getVisitTimeTotal();

        List<Integer> pvList = randomListFromFinalTotal(pvTotal, dates.size());
        List<Long> avgTimeList = randomListFromFinalTotal(visitTimeTotal, ucTotal);

        for (DateTime dateTime : dates) {
            AbstractTjDataEntity tjData;
            if (clazz.equals(TjViewData.class)) {
                tjData = new TjViewData();
            } else {
                tjData = new TjTaskData();
            }
            tjData.setId(new TjDataPk(hourTotalModel.getOrgId(), dateTime));
            //随机PV量
            int randomPv = randomEle(pvList, true);
            tjData.setPvCount(randomPv);

            if (randomPv == 0) {
                tjData.setScCount(0).setDcCount(0).setDdcCount(0).setUvCount(0).setUcCount(0).setVisitTime(0);
            } else {
                int randomSc = RandomUtil.randomInt(randomPv);
                randomSc = randomSc > scTotal ? scTotal : randomSc;
                tjData.setScCount(randomSc);
                scTotal -= randomSc;

                int randomDc = RandomUtil.randomInt(randomPv);
                randomDc = randomDc > dcTotal ? dcTotal : randomDc;
                tjData.setDcCount(randomDc);
                dcTotal -= randomDc;

                int randomDdc = RandomUtil.randomInt(randomPv);
                randomDdc = randomDdc > ddcTotal ? ddcTotal : randomDdc;
                tjData.setDdcCount(randomDdc);
                ddcTotal -= randomDdc;

                int randomUv = RandomUtil.randomInt(randomPv);
                randomUv = randomUv > uvTotal ? uvTotal : randomUv;
                tjData.setUvCount(randomUv);
                uvTotal -= randomUv;

                int randomUc = RandomUtil.randomInt(randomPv);
                randomUc = randomUc > ucTotal ? ucTotal : randomUc;
                tjData.setUcCount(randomUc);
                ucTotal -= randomUc;

                List<Long> randomAvgList = randomEle(avgTimeList, randomUc, true);
                long randomAvg = randomAvgList.stream().reduce((a, b) -> a + b).orElse(0L);
                tjData.setVisitTime(randomAvg);
            }
            tjDataList.add(tjData);
        }
        return tjDataList;
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
     * 从列表中随机取出一组元素，并删除该元素
     *
     * @param list
     * @param remove
     * @param <T>
     * @return
     */
    public static <T> List<T> randomEle(List<T> list, int limit, boolean remove) {
        List<T> els = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            els.add(randomEle(list, remove));
        }
        return els;
    }
}

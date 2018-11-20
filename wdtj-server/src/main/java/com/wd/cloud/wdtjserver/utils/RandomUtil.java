package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.HourTotalModel;
import com.wd.cloud.wdtjserver.model.WeightModel;

import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public class RandomUtil extends cn.hutool.core.util.RandomUtil {

    private static final Log log = LogFactory.get();

    /**
     * 生成总和为固定值的随机数列表
     *
     * @param total 随机数之和
     * @param count 随机数的个数
     * @return
     */
    public static List<Long> randomLongListFromFinalTotal(long total, int count) {
        List<Long> tempList = new ArrayList<>();
        if (total == 0 || count == 0) {
            log.warn("随机总和：{},随机个数：{}", total, count);
            return tempList;
        }
        log.info("随机总和：{},随机个数：{}", total, count);
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
    public static List<Integer> randomIntListFromFinalTotal(int total, int count) {
        List<Integer> tempList = new ArrayList<>();
        if (total == 0 || count == 0) {
            log.warn("随机总和：{},随机个数：{}", total, count);
            return tempList;
        }
        log.info("随机总和：{},随机个数：{}", total, count);
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
        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * ucTotal;
        List<Long> avgTimeRandomList = randomLongListFromFinalTotal(avgTimeTotal, ucTotal);
        // 计算平均值
        double pvAvg = getAvg(pvTotal, weightMap.size(), avgWeight);
        double scAvg = getAvg(scTotal, weightMap.size(), avgWeight);
        double dcAvg = getAvg(dcTotal, weightMap.size(), avgWeight);
        double ddcAvg = getAvg(ddcTotal, weightMap.size(), avgWeight);
        double uvAvg = getAvg(uvTotal, weightMap.size(), avgWeight);
        double ucAvg = getAvg(ucTotal, weightMap.size(), avgWeight);

        int lunShu = 0;
        while (pvTotal > 0 || scTotal > 0 || dcTotal > 0 || ddcTotal > 0 || uvTotal > 0 || ucTotal > 0 || avgTimeRandomList.size() > 0) {
            log.info("第{}轮循环。。。。。", ++lunShu);
            log.info("pvTotal={},scTotal={},dcTotal={},ddcTotal={},uvTotal={},ucTotal={},avgTimeRandomList.size()={}", pvTotal, scTotal, dcTotal, ddcTotal, uvTotal, ucTotal, avgTimeRandomList.size());
            for (Map.Entry<WeightModel, HourTotalModel> entry : weightMap.entrySet()) {
                // 权重
                double weight = entry.getKey().getValue();
                //随机PV量
                int randomPv = getRandomIntFromAvg(pvAvg, weight, ratio);
                // 如果随机值大于总值，那么随机值=总值
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
                    int randomSc = getRandomIntFromAvg(scAvg, weight, ratio);
                    // 如果随机值大于总值，那么随机值=总值
                    int scCount = randomSc > scTotal ? scTotal : randomSc;
                    // 随机搜索量必须小于PV量
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

                    int randomUv = getRandomIntFromAvg(uvAvg, weight, ratio);
                    int uvCount = randomUv > uvTotal ? uvTotal : randomUv;
                    if (entry.getValue().getUvTotal() + uvCount > entry.getValue().getPvTotal()) {
                        uvCount = entry.getValue().getPvTotal() - entry.getValue().getUvTotal();
                    }
                    entry.getValue().setUvTotal(entry.getValue().getUvTotal() + uvCount);
                    uvTotal -= uvCount;

                    int randomUc = getRandomIntFromAvg(ucAvg, weight, ratio);
                    int ucCount = randomUc > ucTotal ? ucTotal : randomUc;
                    if (entry.getValue().getUcTotal() + ucCount > entry.getValue().getPvTotal()) {
                        ucCount = entry.getValue().getPvTotal() - entry.getValue().getUcTotal();
                    }
                    entry.getValue().setUcTotal(entry.getValue().getUcTotal() + ucCount);
                    ucTotal -= ucCount;

                    entry.getValue().setVisitTimeTotal(entry.getValue().getVisitTimeTotal() + randomLongEles(avgTimeRandomList, ucCount, true).orElse(new ArrayList<>()).stream().reduce((a, b) -> a + b).orElse(0L));
                    if (ucTotal != avgTimeRandomList.size()) {
                        log.error("{}个数不相等{}", ucTotal, avgTimeRandomList.size());
                        break;
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

            int randomUv = getRandomIntFromAvg(uvAvg, weight, ratio);
            randomUv = randomPv == 0 ? 0 : randomUv;

            int randomUc = getRandomIntFromAvg(ucAvg, weight, ratio);
            randomUc = randomPv == 0 ? 0 : randomUc;

            long randomVisitTime = getRandomVisitTime(DateUtil.getTimeMillis(tjQuota.getAvgTime()), weight, ratio);
            randomVisitTime = randomPv == 0 ? 0 : randomVisitTime;

            entry.getValue().setPvTotal(randomPv)
                    .setScTotal(randomSc)
                    .setDcTotal(randomDc)
                    .setDdcTotal(randomDdc)
                    .setUvTotal(randomUv)
                    .setUcTotal(randomUc)
                    .setVisitTimeTotal(randomVisitTime)
                    .setOrgId(tjQuota.getOrgId());
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
        return (int) Math.round(RandomUtil.randomDouble(min, max > 1.5 ? max : 1.5));
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
        return Math.round(RandomUtil.randomDouble(min, max > 1.5 ? max : 1.5));
    }


    /**
     * 生成每分钟的随机数据
     *
     * @param hourTotalModel
     * @return
     */
    public static List<AbstractTjDataEntity> buildMinuteData(HourTotalModel hourTotalModel, Class clazz) {

        Map<DateTime, AbstractTjDataEntity> tjDataMap = new HashMap<>();
        //获取分钟列表60个
        List<DateTime> dates = DateUtil.rangeMinuteFromHours(hourTotalModel.getHourDate());
        dates.forEach(dateTime -> {
            if (clazz.equals(TjViewData.class)) {
                tjDataMap.put(dateTime, new TjViewData());
            } else {
                tjDataMap.put(dateTime, new TjTaskData());
            }
        });
        int pvTotal = hourTotalModel.getPvTotal();
        int scTotal = hourTotalModel.getScTotal();
        int dcTotal = hourTotalModel.getDcTotal();
        int ddcTotal = hourTotalModel.getDdcTotal();
        int uvTotal = hourTotalModel.getUvTotal();
        int ucTotal = hourTotalModel.getUcTotal();
        long visitTimeTotal = hourTotalModel.getVisitTimeTotal();

        List<Integer> pvList = randomIntListFromFinalTotal(pvTotal, dates.size());
        // 平均访问时长 = 总访问时长/访问次数
        List<Long> avgTimeList = randomLongListFromFinalTotal(visitTimeTotal, ucTotal);
        int lunShu = 0;
        while (pvList.size() > 0 || scTotal > 0 || dcTotal > 0 || ddcTotal > 0 || uvTotal > 0 || ucTotal > 0 || avgTimeList.size()>0) {
            log.info("第{}轮循环。。。。。", ++lunShu);
            log.info("pvList.size()={},scTotal={},dcTotal={},ddcTotal={},uvTotal={},ucTotal={},avgTimeList.size()={}", pvList.size(), scTotal, dcTotal, ddcTotal, uvTotal, ucTotal, avgTimeList.size());
            for (Map.Entry<DateTime, AbstractTjDataEntity> entry : tjDataMap.entrySet()) {
                entry.getValue().setId(new TjDataPk(hourTotalModel.getOrgId(), entry.getKey()));
                //随机拿出一个PV量
                int randomPv = randomIntEle(pvList, true).orElse(0);
                entry.getValue().setPvCount(entry.getValue().getPvCount() + randomPv);

                if (entry.getValue().getPvCount() == 0) {
                    entry.getValue().setScCount(0).setDcCount(0).setDdcCount(0).setUvCount(0).setUcCount(0).setVisitTime(DateUtil.createTime(0));
                } else {
                    int randomSc = (int) Math.round(RandomUtil.randomQuota(entry.getValue().getPvCount()));
                    randomSc = randomSc > scTotal ? scTotal : randomSc;
                    entry.getValue().setScCount(entry.getValue().getScCount() + randomSc);
                    scTotal -= randomSc;

                    int randomDc = (int) Math.round(RandomUtil.randomQuota(entry.getValue().getPvCount()));
                    randomDc = randomDc > dcTotal ? dcTotal : randomDc;
                    entry.getValue().setDcCount(entry.getValue().getDcCount() + randomDc);
                    dcTotal -= randomDc;

                    int randomDdc = (int) Math.round(RandomUtil.randomQuota(entry.getValue().getPvCount()));
                    randomDdc = randomDdc > ddcTotal ? ddcTotal : randomDdc;
                    entry.getValue().setDdcCount(entry.getValue().getDdcCount() + randomDdc);
                    ddcTotal -= randomDdc;

                    int randomUv = (int) Math.round(RandomUtil.randomQuota(entry.getValue().getPvCount() - entry.getValue().getUvCount()));
                    randomUv = randomUv > uvTotal ? uvTotal : randomUv;
                    entry.getValue().setUvCount(entry.getValue().getUvCount() + randomUv);
                    uvTotal -= randomUv;

                    if (entry.getValue().getUcCount() <= entry.getValue().getUvCount()) {
                        int min = entry.getValue().getUvCount() - entry.getValue().getUcCount();
                        int max = entry.getValue().getPvCount() - entry.getValue().getUcCount();
                        int randomUc = (int) Math.round(RandomUtil.randomQuota(min, max));
                        randomUc = randomUc > ucTotal ? ucTotal : randomUc;
                        entry.getValue().setUcCount(entry.getValue().getUcCount() + randomUc);
                        ucTotal -= randomUc;

                        List<Long> randomAvgList = randomLongEles(avgTimeList, randomUc, true).orElse(new ArrayList<>());
                        long randomAvg = randomAvgList.stream().reduce((a, b) -> a + b).orElse(0L);
                        entry.getValue().setVisitTime(DateUtil.createTime(DateUtil.getTimeMillis(entry.getValue().getVisitTime()) + randomAvg));
                    }
                }
            }
        }

        return new ArrayList<>(tjDataMap.values());
    }


    public static double randomQuota(int limit) {
        if (limit == 0) {
            return 0;
        } else {
            return RandomUtil.randomDouble(limit>1.5?limit:1.5);
        }
    }

    public static double randomQuota(int min, int max) {
        if (min == max) {
            return randomQuota(max);
        } else {
            return RandomUtil.randomDouble(min, max);
        }
    }

    /**
     * 从列表中随机取出一个元素，并删除该元素
     *
     * @param list
     * @param remove
     * @return
     */
    public static Optional<Integer> randomIntEle(List<Integer> list, boolean remove) {
        if (list != null && list.size() > 0) {
            int index = randomInt(list.size());
            int e = list.get(index);
            if (remove) {
                list.remove(index);
            }
            return Optional.of(e);
        } else {
            log.debug("列表:{}为空", list);
            return Optional.ofNullable(null);
        }
    }

    /**
     * 从列表中随机取出一个元素，并删除该元素
     *
     * @param list
     * @param remove
     * @return
     */
    public static Optional<Long> randomLongEle(List<Long> list, boolean remove) {
        if (list != null && list.size() > 0) {
            int index = randomInt(list.size());
            long e = list.get(index);
            if (remove) {
                list.remove(index);
            }
            return Optional.of(e);
        } else {
            log.debug("列表:{}为空", list);
            return Optional.ofNullable(null);
        }
    }

    /**
     * 从列表中随机取出一组元素，如果元素个数不足，取出所有
     *
     * @param list
     * @param remove
     * @return
     */
    public static Optional<List<Long>> randomLongEles(List<Long> list, int limit, boolean remove) {
        if (list != null && list.size() > 0) {
            List<Long> els = new ArrayList<>();
            if (list.size() >= limit) {
                for (int i = 0; i < limit; i++) {
                    long t = randomLongEle(list, remove).orElse(0L);
                    els.add(t);
                }
            } else {
                log.debug("列表:{}元素个数不足{}个", list, limit);
                for (int i = 0; i < list.size(); i++) {
                    long t = randomLongEle(list, remove).orElse(0L);
                    els.add(t);
                }
            }
            return Optional.of(els);
        } else {
            log.debug("列表:{}为空", list);
            return Optional.ofNullable(null);
        }
    }
}

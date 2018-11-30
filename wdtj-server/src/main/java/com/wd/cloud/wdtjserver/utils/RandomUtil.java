package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.HourTotalModel;

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
     * 生成每分钟的随机数据
     *
     * @param hourTotalModel
     * @return
     */
    public static List<TjTaskData> buildDataFromWeight(HourTotalModel hourTotalModel) {
        int pvTotal = hourTotalModel.getPvTotal();
        int scTotal = hourTotalModel.getScTotal();
        int dcTotal = hourTotalModel.getDcTotal();
        int ddcTotal = hourTotalModel.getDdcTotal();
        int uvTotal = hourTotalModel.getUvTotal();
        int ucTotal = hourTotalModel.getUcTotal();

        Map<DateTime, TjTaskData> tjDataEntityMap = new HashMap<>();
        List<WeightRandom.WeightObj<DateTime>> weightObjs = new ArrayList<>();
        //获取分钟数列表
        List<DateTime> minuteList = DateUtil.rangeMinuteFromHours(hourTotalModel.getHourDate());
        minuteList.forEach(minuteDate -> {
            TjTaskData tjData = new TjTaskData();
            TjDataPk tjDataPk = new TjDataPk(hourTotalModel.getOrgId(), minuteDate);
            tjData.setId(tjDataPk);
            tjData.setOrgName(hourTotalModel.getOrgName());
            tjDataEntityMap.put(minuteDate, tjData);
            WeightRandom.WeightObj<DateTime> weightObj = new WeightRandom.WeightObj<>(minuteDate, 1.0);
            weightObjs.add(weightObj);
        });

        // 平均访问时长 = 总访问时长/访问次数
        long visitTimeTotal = hourTotalModel.getVisitTimeTotal();
        List<Long> avgTimeList = randomLongListFromFinalTotal(visitTimeTotal, ucTotal);

        while (pvTotal > 0 || scTotal > 0 || uvTotal > 0 || ucTotal > 0 || dcTotal > 0 || ddcTotal > 0) {
            DateTime minuteDate = RandomUtil.weightRandom(weightObjs).next();
            if (pvTotal > 0) {
                tjDataEntityMap.get(minuteDate).setPvCount(tjDataEntityMap.get(minuteDate).getPvCount() + 1);
                pvTotal--;
            }
            if (scTotal > 0) {
                tjDataEntityMap.get(minuteDate).setScCount(tjDataEntityMap.get(minuteDate).getScCount() + 1);
                scTotal--;
            }
            if (uvTotal > 0) {
                tjDataEntityMap.get(minuteDate).setUvCount(tjDataEntityMap.get(minuteDate).getUvCount() + 1);
                uvTotal--;
            }
            if (ucTotal > 0) {
                tjDataEntityMap.get(minuteDate).setUcCount(tjDataEntityMap.get(minuteDate).getUcCount() + 1);
                long randomVisitTime = RandomUtil.randomLongEle(avgTimeList, true).orElse(0L);
                randomVisitTime += tjDataEntityMap.get(minuteDate).getVisitTime();
                tjDataEntityMap.get(minuteDate).setVisitTime(randomVisitTime);
                ucTotal--;
            }
            if (dcTotal > 0) {
                int dcCount = RandomUtil.randomInt(3);
                dcCount = dcCount > dcTotal ? dcTotal : dcCount;
                tjDataEntityMap.get(minuteDate).setDcCount(tjDataEntityMap.get(minuteDate).getDcCount() + dcCount);
                dcTotal -= dcCount;
            }
            if (ddcTotal > 0) {
                int ddcCount = RandomUtil.randomInt(3);
                ddcCount = ddcCount > dcTotal ? dcTotal : ddcCount;
                tjDataEntityMap.get(minuteDate).setDdcCount(tjDataEntityMap.get(minuteDate).getDdcCount() + ddcCount);
                ddcTotal -= ddcCount;
            }
        }
        return new ArrayList<>(tjDataEntityMap.values());
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


    public static List<WeightRandom.WeightObj<DateTime>> getWeightList(Map<String, Double> settingMap, List<DateTime> minuteList) {
        List<WeightRandom.WeightObj<DateTime>> minuteWeightList = new ArrayList<>();
        //计算每分钟比率值放入map中
        minuteList.forEach(minute -> {
            String monthKey = "1-" + (DateUtil.month(minute) + 1);
            String dayKey = "2-" + (DateUtil.dayOfMonth(minute));
            // 周日-周六：1 - 7
            String weekKey = "3-" + (DateUtil.dayOfWeek(minute));
            String hoursKey = "4-" + DateUtil.hour(minute, true);
            double monthWeight = settingMap.get(monthKey) == null ? 1.0 : settingMap.get(monthKey);
            double dayWeight = settingMap.get(dayKey) == null ? 1.0 : settingMap.get(dayKey);
            double weekWeight = settingMap.get(weekKey) == null ? 1.0 : settingMap.get(weekKey);
            double hourWeight = settingMap.get(hoursKey) == null ? 1.0 : settingMap.get(hoursKey);
            double weight = (monthWeight + dayWeight + weekWeight + hourWeight) / 4.0;
            WeightRandom.WeightObj<DateTime> weightModel = new WeightRandom.WeightObj<>(minute, weight);
            minuteWeightList.add(weightModel);
        });
        return minuteWeightList;
    }


    /**
     * 历史记录手动生成每分钟访问记录，（总量固定）
     *
     * @param minuteWeightList 分钟的权重列表
     * @return 每分钟访问记录
     */
    public static List<TjViewData> buildHisDataFromWeight(TjHisQuota tjHisQuota, List<WeightRandom.WeightObj<DateTime>> minuteWeightList) {

        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        int uvTotal = tjHisQuota.getUvCount();
        int ucTotal = tjHisQuota.getUcCount();

        Map<DateTime, TjViewData> tjDataEntityMap = new HashMap<>();

        minuteWeightList.forEach(minuteDate -> {
            TjViewData tjData = new TjViewData();
            TjDataPk tjDataPk = new TjDataPk(tjHisQuota.getOrgId(), minuteDate.getObj());
            tjData.setId(tjDataPk);
            tjData.setOrgName(tjHisQuota.getOrgName());
            tjDataEntityMap.put(minuteDate.getObj(), tjData);
        });
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * ucTotal;
        // 随机生成：size为访问次数且总和等于总时间的随机列表
        List<Long> avgTimeList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, ucTotal);

        log.info("开始：pv={},sc={},dc={},ddc={},uv={},uc={}", pvTotal, scTotal, dcTotal, ddcTotal, uvTotal, ucTotal);
        while (pvTotal > 0 || scTotal > 0 || uvTotal > 0 || ucTotal > 0 || dcTotal > 0 || ddcTotal > 0) {
            DateTime minuteDate = RandomUtil.weightRandom(minuteWeightList).next();
            if (pvTotal > 0) {
                tjDataEntityMap.get(minuteDate).setPvCount(tjDataEntityMap.get(minuteDate).getPvCount() + 1);
                pvTotal--;
            }
            if (scTotal > 0) {
                tjDataEntityMap.get(minuteDate).setScCount(tjDataEntityMap.get(minuteDate).getScCount() + 1);
                scTotal--;
            }
            if (uvTotal > 0) {
                tjDataEntityMap.get(minuteDate).setUvCount(tjDataEntityMap.get(minuteDate).getUvCount() + 1);
                uvTotal--;
            }
            if (ucTotal > 0) {
                tjDataEntityMap.get(minuteDate).setUcCount(tjDataEntityMap.get(minuteDate).getUcCount() + 1);
                long randomVisitTime = RandomUtil.randomLongEle(avgTimeList, true).orElse(0L);
                randomVisitTime += tjDataEntityMap.get(minuteDate).getVisitTime();
                tjDataEntityMap.get(minuteDate).setVisitTime(randomVisitTime);
                ucTotal--;
            }
            if (dcTotal > 0) {
                int dcCount = RandomUtil.randomInt(3);
                dcCount = dcCount > dcTotal ? dcTotal : dcCount;
                tjDataEntityMap.get(minuteDate).setDcCount(tjDataEntityMap.get(minuteDate).getDcCount() + dcCount);
                dcTotal -= dcCount;
            }
            if (ddcTotal > 0) {
                int ddcCount = RandomUtil.randomInt(3);
                ddcCount = ddcCount > dcTotal ? dcTotal : ddcCount;
                tjDataEntityMap.get(minuteDate).setDdcCount(tjDataEntityMap.get(minuteDate).getDdcCount() + ddcCount);
                ddcTotal -= ddcCount;
            }
        }
        log.info("结束：pv={},sc={},dc={},ddc={},uv={},uc={}", pvTotal, scTotal, dcTotal, ddcTotal, uvTotal, ucTotal);
        return new ArrayList<>(tjDataEntityMap.values());
    }
}

package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.TotalModel;

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
     * 根据权重列表生成固定总量的数组
     *
     * @param weightList
     * @param total
     * @param fuDong     浮动指数
     * @return
     */
    public static <T> Map<T, Integer> randomListFromWeight(List<WeightRandom.WeightObj<T>> weightList, Integer total, double fuDong) {
        if (!(fuDong > 0 && fuDong < 1)) {
            throw new IllegalArgumentException("浮动指数必须在0~1之间");
        }
        Map<T, Integer> result = new HashMap<>();
        // 将权重列表倒序排列，保证权重高的优先取值
        weightSort(weightList);
        // 统计权重总和
        double sumWeight = weightList.stream().map(WeightRandom.WeightObj::getWeight).reduce((a, b) -> a + b).orElse(1.0 * weightList.size());
        // 计算平均权重
        double avgWeight = sumWeight / weightList.size();
        // 计算平均值
        double avg = 1.0 * total / weightList.size();
        int before = 0;
        for (int i = 0; i < weightList.size() - 1; i++) {
            // 实际值 = 平均值 * 权重 / 平均权重
            double countForWeight = avg * weightList.get(i).getWeight() / avgWeight;
            double min = before + countForWeight * (1 - fuDong);
            double max = before + countForWeight * (1 + fuDong);
            min = min > total ? before : min;
            max = max > total ? total : max;
            int after = min < max ? (int) Math.round(RandomUtil.randomDouble(min, max)) : total;
            result.put(weightList.get(i).getObj(), after - before);
            before = after;
        }
        // 最后一个的值
        result.put(weightList.get(weightList.size() - 1).getObj(), total - before);
        return result;
    }


    /**
     * 根据权重列表生成固定总量的数组
     *
     * @param weightList
     * @param tjHisQuota
     * @param fuDong     浮动指数
     * @return
     */
    public static <T> Map<T, TotalModel> biuldTotalModelFromWeight(List<WeightRandom.WeightObj<T>> weightList, TjHisQuota tjHisQuota, double fuDong) {
        if (!(fuDong > 0 && fuDong < 1)) {
            throw new IllegalArgumentException("浮动指数必须在0~1之间");
        }
        Map<T, TotalModel> dayTotalMap = new HashMap<>();
        weightList.forEach(day -> {
            TotalModel totalModel = new TotalModel();
            totalModel.setOrgId(tjHisQuota.getOrgId()).setOrgName(tjHisQuota.getOrgName());
            dayTotalMap.put(day.getObj(), totalModel);
        });

        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        int uvTotal = tjHisQuota.getUvCount();
        int vvTotal = tjHisQuota.getVvCount();
        boolean isMaxSc = false;
        if (scTotal > vvTotal) {
            isMaxSc = true;
        }
        int spvTotal = scTotal > vvTotal ? pvTotal - scTotal : pvTotal - vvTotal;
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * vvTotal;
        // 随机生成：size为访问次数且总和等于总时间的随机列表
        List<Long> avgTimeList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, vvTotal);

        Map<T, Integer> result = new HashMap<>();
        // 将权重列表倒序排列，保证权重高的优先取值
        weightSort(weightList);
        // 统计权重总和
        double sumWeight = weightList.stream().map(WeightRandom.WeightObj::getWeight).reduce((a, b) -> a + b).orElse(1.0 * weightList.size());
        // 计算平均权重
        double avgWeight = sumWeight / weightList.size();
        // 计算平均值
        double avgPv = 1.0 * pvTotal / weightList.size(),
                avgSc = 1.0 * scTotal / weightList.size(),
                avgDc = 1.0 * dcTotal / weightList.size(),
                avgDdc = 1.0 * ddcTotal / weightList.size(),
                avgUv = 1.0 * uvTotal / weightList.size(),
                avgVv = 1.0 * vvTotal / weightList.size(),
                avgSpv = 1.0 * spvTotal / weightList.size();


        int uvBefore = 0, vvBefore = 0, scBefore = 0, pvBefore = 0, spvBefore = 0, dcBefore = 0, ddcBefore = 0;

        for (int i = 0; i < weightList.size() - 1; i++) {
            // 实际值 = 平均值 * 权重 / 平均权重
            double uvCountForWeight = avgUv * weightList.get(i).getWeight() / avgWeight;

            double uvMin = uvBefore + uvCountForWeight * (1 - fuDong);
            double uvMax = uvBefore + uvCountForWeight * (1 + fuDong);
            uvMin = uvMin > uvTotal ? uvBefore : uvMin;
            uvMax = uvMax > uvTotal ? uvTotal : uvMax;
            int uvAfter = uvMin < uvMax ? (int) Math.round(RandomUtil.randomDouble(uvMin, uvMax)) : uvTotal;
            int uvCount = uvAfter - uvBefore;
            uvBefore = uvAfter;

            double vvCountForWeight = avgVv * weightList.get(i).getWeight() / avgWeight;
            double vvMin = uvMin;
            double vvMax = vvBefore + vvCountForWeight * (1 + fuDong);
            vvMin = vvMin > vvTotal ? vvBefore : vvMin;
            vvMax = vvMax > vvTotal ? vvTotal : vvMax;
            int vvAfter = vvMin < vvMax ? (int) Math.round(RandomUtil.randomDouble(vvMin, vvMax)) : vvTotal;
            int vvCount = vvAfter - vvBefore;
            List<Long> visitTimeList = RandomUtil.randomLongEles(avgTimeList, vvCount, true).orElse(new ArrayList<>());
            long visitCount = visitTimeList.stream().reduce((a, b) -> a + b).orElse(0L);
            vvBefore = vvAfter;

            double scCountForWeight = avgSc * weightList.get(i).getWeight() / avgWeight;
            double scMin = scBefore + scCountForWeight * (1 - fuDong);
            double scMax = scBefore + scCountForWeight * (1 + fuDong);
            scMin = scMin > scTotal ? scBefore : scMin;
            scMax = scMax > scTotal ? scTotal : scMax;
            int scAfter = scMin < scMax ? (int) Math.round(RandomUtil.randomDouble(scMin, scMax)) : scTotal;
            int scCount = scAfter - scBefore;
            scBefore = scAfter;

            double spvCountForWeight = avgSpv * weightList.get(i).getWeight() / avgWeight;
            //pv量不小于 vv 和 sc
            double spvMin = spvBefore + spvCountForWeight * (1 - fuDong);
            double spvMax = spvBefore + spvCountForWeight * (1 + fuDong);
            spvMin = spvMin > spvTotal ? spvBefore : spvMin;
            spvMax = spvMax > spvTotal ? spvTotal : spvMax;
            int spvAfter = spvMin < spvMax ? (int) Math.round(RandomUtil.randomDouble(spvMin, spvMax)) : spvTotal;
            int pvCount = isMaxSc ? scCount + spvAfter - spvBefore : vvCount + spvAfter - spvBefore;
            pvBefore -= pvCount;
            spvBefore = spvAfter;


            double dcCountForWeight = avgDc * weightList.get(i).getWeight() / avgWeight;
            double dcMin = dcBefore + dcCountForWeight * (1 - fuDong);
            double dcMax = dcBefore + dcCountForWeight * (1 + fuDong);
            dcMin = dcMin > dcTotal ? dcBefore : dcMin;
            dcMax = dcMax > dcTotal ? dcTotal : dcMax;
            int dcAfter = dcMin < dcMax ? (int) Math.round(RandomUtil.randomDouble(dcMin, dcMax)) : dcTotal;
            int dcCount = dcAfter - dcBefore;
            dcBefore = dcAfter;

            double ddcCountForWeight = avgDdc * weightList.get(i).getWeight() / avgWeight;
            double ddcMin = ddcBefore + ddcCountForWeight * (1 - fuDong);
            double ddcMax = ddcBefore + ddcCountForWeight * (1 + fuDong);
            ddcMin = ddcMin > ddcTotal ? ddcBefore : ddcMin;
            ddcMax = ddcMax > ddcTotal ? ddcTotal : ddcMax;
            int ddcAfter = ddcMin < ddcMax ? (int) Math.round(RandomUtil.randomDouble(ddcMin, ddcMax)) : ddcTotal;
            int ddcCount = ddcAfter - ddcBefore;
            ddcBefore = ddcAfter;
            dayTotalMap.get(weightList.get(i).getObj())
                    .setUvTotal(uvCount)
                    .setVvTotal(vvCount)
                    .setScTotal(scCount)
                    .setPvTotal(pvCount)
                    .setDcTotal(dcCount)
                    .setDdcTotal(ddcCount)
                    .setVisitTimeTotal(visitCount);
        }
        // 最后一个的值
        dayTotalMap.get(weightList.get(weightList.size() - 1).getObj())
                .setScTotal(scTotal - scBefore)
                .setUvTotal(uvTotal - uvBefore)
                .setVvTotal(vvTotal - vvBefore)
                .setDcTotal(dcTotal - dcBefore)
                .setDdcTotal(ddcTotal - ddcBefore)
                .setPvTotal(pvTotal - pvBefore)
                .setVisitTimeTotal(avgTimeList.stream().reduce((a, b) -> a + b).orElse(0L));
        return dayTotalMap;
    }

    private static <T> void weightSort(List<WeightRandom.WeightObj<T>> weightList) {
        weightList.sort(new Comparator<WeightRandom.WeightObj>() {
            @Override
            public int compare(WeightRandom.WeightObj o1, WeightRandom.WeightObj o2) {
                if (o1.getWeight() > o2.getWeight()) {
                    return -1;
                } else if (o1.getWeight() < o2.getWeight()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }


    /**
     * 生成每分钟的随机数据
     *
     * @param hourTotalModel
     * @return
     */
    public static List<TjTaskData> buildDataFromWeight(TotalModel hourTotalModel) {
        int pvTotal = hourTotalModel.getPvTotal();
        int scTotal = hourTotalModel.getScTotal();
        int dcTotal = hourTotalModel.getDcTotal();
        int ddcTotal = hourTotalModel.getDdcTotal();
        int uvTotal = hourTotalModel.getUvTotal();
        int vvTotal = hourTotalModel.getVvTotal();

        Map<DateTime, TjTaskData> tjDataEntityMap = new HashMap<>();
        List<WeightRandom.WeightObj<DateTime>> weightObjs = new ArrayList<>();
        //获取分钟数列表
        List<DateTime> minuteList = DateUtil.rangeMinuteFromHours(hourTotalModel.getDate());
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
        List<Long> avgTimeList = randomLongListFromFinalTotal(visitTimeTotal, vvTotal);

        while (pvTotal > 0 || scTotal > 0 || uvTotal > 0 || vvTotal > 0 || dcTotal > 0 || ddcTotal > 0) {
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
            if (vvTotal > 0) {
                tjDataEntityMap.get(minuteDate).setVvCount(tjDataEntityMap.get(minuteDate).getVvCount() + 1);
                long randomVisitTime = RandomUtil.randomLongEle(avgTimeList, true).orElse(0L);
                randomVisitTime += tjDataEntityMap.get(minuteDate).getVisitTime();
                tjDataEntityMap.get(minuteDate).setVisitTime(randomVisitTime);
                vvTotal--;
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


    /**
     * 计算天的权重列表
     *
     * @param weightMap 权重配置
     * @param dayList   列表（天）
     * @return
     */
    public static List<WeightRandom.WeightObj<DateTime>> dayWeightList(Map<String, Double> weightMap, List<DateTime> dayList) {
        List<WeightRandom.WeightObj<DateTime>> dayWeightList = new ArrayList<>();
        //计算每分钟比率值放入map中
        dayList.forEach(day -> {
            String monthKey = "1-" + (DateUtil.month(day) + 1);
            // 周日-周六：1 - 7
            String weekKey = "2-" + (DateUtil.dayOfWeek(day));
            String dayKey = "3-" + (DateUtil.dayOfMonth(day));
            double monthWeight = weightMap.get(monthKey) == null ? 1.0 : weightMap.get(monthKey);
            double weekWeight = weightMap.get(weekKey) == null ? 1.0 : weightMap.get(weekKey);
            double dayWeight = weightMap.get(dayKey) == null ? 1.0 : weightMap.get(dayKey);
            double weight = monthWeight * weekWeight * dayWeight;
            WeightRandom.WeightObj<DateTime> weightModel = new WeightRandom.WeightObj<>(day, weight);
            dayWeightList.add(weightModel);
        });
        return dayWeightList;
    }


    /**
     * 根据日权重生成日总量
     *
     * @param tjHisQuota
     * @param dayWeightList
     * @return
     */
    public static Map<DateTime, TotalModel> dayTotalFromWeight(TjHisQuota tjHisQuota, List<WeightRandom.WeightObj<DateTime>> dayWeightList) {
        // 开始时间和结束时间如果不满一天，需要根据所占一天比列重新计算一天所占权重
        DateTime beginTimeFromEndOfDay = DateUtil.endOfDay(tjHisQuota.getBeginTime());
        DateTime endTimeFromBeginOfDay = DateUtil.beginOfDay(tjHisQuota.getEndTime());
        // 开始时间和结束时间在这一天中所占的分钟数
        long minutesForBegin = DateUtil.between(tjHisQuota.getBeginTime(), beginTimeFromEndOfDay, DateUnit.MINUTE) + 1;
        long minutesForEnd = DateUtil.between(endTimeFromBeginOfDay, tjHisQuota.getEndTime(), DateUnit.MINUTE) + 1;
        // 一天的总分钟数
        long dayMinutes = 60 * 24;
        // 开始时间所占一天中的比重
        double beginWeight = 1.0 * minutesForBegin / dayMinutes;
        double endWeight = 1.0 * minutesForEnd / dayMinutes;
        int dayWeightListSize = dayWeightList.size();
        //开始时间和结束时间新的权重
        WeightRandom.WeightObj<DateTime> beginDayWeight = new WeightRandom.WeightObj<>(dayWeightList.get(0).getObj(), dayWeightList.get(0).getWeight() * beginWeight);
        WeightRandom.WeightObj<DateTime> endDayWeight = new WeightRandom.WeightObj<>(dayWeightList.get(dayWeightListSize - 1).getObj(), dayWeightList.get(dayWeightListSize - 1).getWeight() * endWeight);
        dayWeightList.set(0, beginDayWeight);
        dayWeightList.set(dayWeightListSize - 1, endDayWeight);
        Map<DateTime, TotalModel> dayTotalMap = biuldTotalModelFromWeight(dayWeightList, tjHisQuota, 0.3);
//        Map<DateTime, TotalModel> dayTotalMap = new HashMap<>();
//        dayWeightList.forEach(day -> {
//            TotalModel totalModel = new TotalModel();
//            totalModel.setOrgId(tjHisQuota.getOrgId()).setOrgName(tjHisQuota.getOrgName()).setDate(day.getObj());
//            dayTotalMap.put(day.getObj(), totalModel);
//        });
//        int pvTotal = tjHisQuota.getPvCount();
//        int scTotal = tjHisQuota.getScCount();
//        int dcTotal = tjHisQuota.getDcCount();
//        int ddcTotal = tjHisQuota.getDdcCount();
//        int uvTotal = tjHisQuota.getUvCount();
//        int vvTotal = tjHisQuota.getVvCount();
//        // 计算用户访问总时间 = 平均访问时间 * 访问次数
//        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * vvTotal;
//        // 随机生成：size为访问次数且总和等于总时间的随机列表
//        List<Long> avgTimeList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, vvTotal);
//
//
//        Map<DateTime, Integer> scMap = randomListFromWeight(dayWeightList, scTotal, 0.3);
//        scMap.forEach((k, v) -> {
//            // 同时设置sc和pv量
//            dayTotalMap.get(k).setScTotal(v).setPvTotal(v);
//        });
//        // uv和vv
//        Map<DateTime, Integer> uvMap = randomListFromWeight(dayWeightList, uvTotal, 0.3);
//        uvMap.forEach((k, v) -> {
//            //同时设置uv和vv量
//            dayTotalMap.get(k).setUvTotal(v).setVvTotal(v);
//        });
//        int svvTotal = vvTotal - uvTotal;
//        Map<DateTime, Integer> vvMap = randomListFromWeight(dayWeightList, svvTotal, 0.3);
//        vvMap.forEach((k, v) -> {
//            // 在已有vv量基础上加上新的量
//            int yvv = dayTotalMap.get(k).getVvTotal();
//            int sumVv = yvv + v;
//            dayTotalMap.get(k).setVvTotal(sumVv);
//            List<Long> visitTimeList = RandomUtil.randomLongEles(avgTimeList, sumVv, true).orElse(new ArrayList<>());
//            dayTotalMap.get(k).setVisitTimeTotal(visitTimeList.stream().reduce((a, b) -> a + b).orElse(0L));
//        });
//
//        // 剩余pv
//        int spvTotal = pvTotal - scTotal;
//        Map<DateTime, Integer> pvMap = randomListFromWeight(dayWeightList, spvTotal, 0.3);
//        pvMap.forEach((k, v) -> {
//            // 在已有PV量基础上加上新的量
//            int ypv = dayTotalMap.get(k).getPvTotal();
//            dayTotalMap.get(k).setPvTotal(ypv + v);
//        });
//        // 下载量
//        Map<DateTime, Integer> dcMap = randomListFromWeight(dayWeightList, dcTotal, 0.3);
//        dcMap.forEach((k, v) -> {
//            dayTotalMap.get(k).setDcTotal(v);
//        });
//        // 文献传递量
//        Map<DateTime, Integer> ddcMap = randomListFromWeight(dayWeightList, ddcTotal, 0.3);
//        ddcMap.forEach((k, v) -> {
//            dayTotalMap.get(k).setDdcTotal(v);
//        });


        return dayTotalMap;
    }


    /**
     * @param beginTime     开始时间必须是从yyyy-MM-dd 00:00:00
     * @param endTime       结束时间必须是yyyy-MM-dd 23:59:00
     * @param dayTotalModel
     * @return
     */
    public static List<TjViewData> buildMinuteTjData(DateTime beginTime, DateTime endTime, Map.Entry<DateTime, TotalModel> dayTotalModel) {
        List<TjViewData> tjDayDataList = new ArrayList<>();
        //高峰时段分钟数列表
        List<DateTime> highMinutes = getHighMinutes(beginTime, endTime, dayTotalModel.getKey());
//        int highMinuteAllSize = 11 * 60;
//        double highSizeBl = 1.0 * highMinutes.size()/highMinuteAllSize;
        //低谷时段分钟数列表
        List<DateTime> lowMinutes = getLowMinutes(beginTime, endTime, dayTotalModel.getKey());
//        int lowMinuteAllSize = 9 * 60;
//        double lowSizeBl = 1.0 * lowMinutes.size()/lowMinuteAllSize;
        //其它时段分钟数列表
        List<DateTime> otherMinutes = getOtherMinutes(beginTime, endTime, dayTotalModel.getKey());
//        int otherMinuteAllSize = 4 * 60;
//        double otherSizeBl = 1.0 * otherMinutes.size()/otherMinuteAllSize;

        //高峰时段所占比列
        double highWeight = RandomUtil.randomDouble(0.85, 0.92);
        //低谷时段所占比列
        double lowWeight = RandomUtil.randomDouble(0.02, 0.03);
        //其它时段所占比列
        double otherWeight = 1 - highWeight - lowWeight;

        Long orgId = dayTotalModel.getValue().getOrgId();
        String orgName = dayTotalModel.getValue().getOrgName();
        int scTotal = dayTotalModel.getValue().getScTotal();
        int pvTotal = dayTotalModel.getValue().getPvTotal();
        int dcTotal = dayTotalModel.getValue().getDcTotal();
        int ddcTotal = dayTotalModel.getValue().getDdcTotal();
        int uvTotal = dayTotalModel.getValue().getUvTotal();
        int vvTotal = dayTotalModel.getValue().getVvTotal();
        long visitTotal = dayTotalModel.getValue().getVisitTimeTotal();
        // 生成vvTotal个访问时长列表
        List<Long> visitList = RandomUtil.randomLongListFromFinalTotal(visitTotal, vvTotal);

        //-----------------------sc
        int highScTotal = (int) Math.round(highWeight * scTotal);
        int lowScTotal = (int) Math.round(lowWeight * scTotal);
        int otherScTotal = scTotal - highScTotal - lowScTotal;
        List<Integer> highScCountList = RandomUtil.randomIntListFromFinalTotal(highScTotal, highMinutes.size());
        List<Integer> lowScCountList = RandomUtil.randomIntListFromFinalTotal(lowScTotal, lowMinutes.size());
        List<Integer> otherScCountList = RandomUtil.randomIntListFromFinalTotal(otherScTotal, otherMinutes.size());

        //-----------------------pv
        int spvTotal = pvTotal - scTotal;
        int highSpvTotal = (int) Math.round(highWeight * spvTotal);
        int lowSpvTotal = (int) Math.round(lowWeight * spvTotal);
        int otherSpvTotal = spvTotal - highSpvTotal - lowSpvTotal;
        List<Integer> highSpvCountList = RandomUtil.randomIntListFromFinalTotal(highSpvTotal, highMinutes.size());
        List<Integer> lowSpvCountList = RandomUtil.randomIntListFromFinalTotal(lowSpvTotal, lowMinutes.size());
        List<Integer> otherSpvCountList = RandomUtil.randomIntListFromFinalTotal(otherSpvTotal, otherMinutes.size());
        //-----------------------dc
        int highDcTotal = (int) Math.round(highWeight * dcTotal);
        int lowDcTotal = (int) Math.round(lowWeight * dcTotal);
        int otherDcTotal = dcTotal - highDcTotal - lowDcTotal;
        List<Integer> highDcCountList = RandomUtil.randomIntListFromFinalTotal(highDcTotal, highMinutes.size());
        List<Integer> lowDcCountList = RandomUtil.randomIntListFromFinalTotal(lowDcTotal, lowMinutes.size());
        List<Integer> otherDcCountList = RandomUtil.randomIntListFromFinalTotal(otherDcTotal, otherMinutes.size());
        //-----------------------ddc
        int highDdcTotal = (int) Math.round(highWeight * ddcTotal);
        int lowDdcTotal = (int) Math.round(lowWeight * ddcTotal);
        int otherDdcTotal = ddcTotal - highDdcTotal - lowDdcTotal;
        List<Integer> highDdcCountList = RandomUtil.randomIntListFromFinalTotal(highDdcTotal, highMinutes.size());
        List<Integer> lowDdcCountList = RandomUtil.randomIntListFromFinalTotal(lowDdcTotal, lowMinutes.size());
        List<Integer> otherDdcCountList = RandomUtil.randomIntListFromFinalTotal(otherDdcTotal, otherMinutes.size());
        //-----------------------uv
        int highUvTotal = (int) Math.round(highWeight * uvTotal);
        int lowUvTotal = (int) Math.round(lowWeight * uvTotal);
        int otherUvTotal = uvTotal - highUvTotal - lowUvTotal;
        List<Integer> highUvCountList = RandomUtil.randomIntListFromFinalTotal(highUvTotal, highMinutes.size());
        List<Integer> lowUvCountList = RandomUtil.randomIntListFromFinalTotal(lowUvTotal, lowMinutes.size());
        List<Integer> otherUvCountList = RandomUtil.randomIntListFromFinalTotal(otherUvTotal, otherMinutes.size());
        //-----------------------vv
        int svvTotal = vvTotal - uvTotal;
        int highSvvTotal = (int) Math.round(highWeight * svvTotal);
        int lowSvvTotal = (int) Math.round(lowWeight * svvTotal);
        int otherSvvTotal = svvTotal - highSvvTotal - lowSvvTotal;
        List<Integer> highSvvCountList = RandomUtil.randomIntListFromFinalTotal(highSvvTotal, highMinutes.size());
        List<Integer> lowSvvCountList = RandomUtil.randomIntListFromFinalTotal(lowSvvTotal, lowMinutes.size());
        List<Integer> otherSvvCountList = RandomUtil.randomIntListFromFinalTotal(otherSvvTotal, otherMinutes.size());

        highMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjViewDate(orgName, tjDataPk, visitList, highScCountList, highSpvCountList, highDcCountList, highDdcCountList, highUvCountList, highSvvCountList));
        });
        lowMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjViewDate(orgName, tjDataPk, visitList, lowScCountList, lowSpvCountList, lowDcCountList, lowDdcCountList, lowUvCountList, lowSvvCountList));
        });
        otherMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjViewDate(orgName, tjDataPk, visitList, otherScCountList, otherSpvCountList, otherDcCountList, otherDdcCountList, otherUvCountList, otherSvvCountList));
        });
        return tjDayDataList;
    }

    /**
     * 创建tjViewData对象
     *
     * @param orgName
     * @param tjDataPk
     * @param visitList
     * @param highScCountList
     * @param highSpvCountList
     * @param highDcCountList
     * @param highDdcCountList
     * @param highUvCountList
     * @param highSvvCountList
     * @return
     */
    private static TjViewData createTjViewDate(String orgName, TjDataPk tjDataPk, List<Long> visitList, List<Integer> highScCountList, List<Integer> highSpvCountList, List<Integer> highDcCountList, List<Integer> highDdcCountList, List<Integer> highUvCountList, List<Integer> highSvvCountList) {
        TjViewData tjViewData = new TjViewData();
        int scCount = RandomUtil.randomIntEle(highScCountList, true).orElse(0);
        int spvCount = RandomUtil.randomIntEle(highSpvCountList, true).orElse(0);
        int dcCount = RandomUtil.randomIntEle(highDcCountList, true).orElse(0);
        int ddcCount = RandomUtil.randomIntEle(highDdcCountList, true).orElse(0);
        int uvCount = RandomUtil.randomIntEle(highUvCountList, true).orElse(0);
        int svvCount = RandomUtil.randomIntEle(highSvvCountList, true).orElse(0);
        List<Long> visites = RandomUtil.randomLongEles(visitList, uvCount + svvCount, true).orElse(new ArrayList<>());
        long visit = visites.stream().reduce((a, b) -> a + b).orElse(0L);
        tjViewData.setScCount(scCount)
                .setPvCount(scCount + spvCount)
                .setDcCount(dcCount)
                .setDdcCount(ddcCount)
                .setUvCount(uvCount)
                .setVvCount(uvCount + svvCount)
                .setVisitTime(visit)
                .setId(tjDataPk)
                .setOrgName(orgName);
        return tjViewData;
    }


    /**
     * 高峰分钟数列表
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getHighMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 8).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 18).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在18点之后或结束时间在8点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }

        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.MINUTE);
    }

    /**
     * 其它分钟数列表
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getOtherMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 19).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 22).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在22点之后或结束时间在19点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }
        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.MINUTE);
    }

    /**
     * 低谷分钟数列表
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getLowMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 7).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);

        DateTime beginMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        List<DateTime> otherMinutes = new ArrayList<>();
        //开始时间在当天的7点之前 且 结束时间在当天的0点之后
        if (beginTime.before(endMinute) && endTime.after(beginMinute)) {
            beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
            endMinute = endTime.before(endMinute) ? endTime : endMinute;
            otherMinutes = DateUtil.rangeToList(beginMinute, endMinute, DateField.MINUTE);
        }
        //开始时间在当天的24点之前 且 结束时间在当天的23点之后
        if (beginTime.before(endMinute2) && endTime.after(beginMinute2)) {
            beginMinute2 = beginTime.after(beginMinute2) ? beginTime : beginMinute2;
            endMinute2 = endTime.before(endMinute2) ? endTime : endMinute2;
            otherMinutes.addAll(DateUtil.rangeToList(beginMinute2, endMinute2, DateField.MINUTE));
        }
        return otherMinutes;
    }


}

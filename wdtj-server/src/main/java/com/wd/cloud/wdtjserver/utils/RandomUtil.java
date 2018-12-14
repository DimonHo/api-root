package com.wd.cloud.wdtjserver.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.*;
import com.wd.cloud.wdtjserver.model.TotalModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public class RandomUtil extends cn.hutool.core.util.RandomUtil {

    private static final Log log = LogFactory.get();

    private static final double LOW_FU_DONG = 0.1;
    private static final double HIGH_FU_DONG = 0.7;

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
        Console.log(randomList);
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
     * 生成总和为固定值的随机数列表
     *
     * @param total 随机数之和
     * @param count 随机数的个数
     * @return 最低不为0的列表
     */
    public static List<Integer> randomIntListFromFinalTotal(int total, int count, double lowFuDong, double highFuDong) {
        List<Integer> randomList = new ArrayList<>();
        if (total == 0 || count == 0) {
            log.warn("随机总和：{},随机个数：{}", total, count);
            return randomList;
        }
        log.info("随机总和：{},随机个数：{}", total, count);
        boolean flag = false;
        // 总量为数量的10倍以上，最低保留一个
        if (total > count * 10) {
            flag = true;
            total = total - count;
        }
        double avg = 1.0 * total / count;
        int before = 0;
        //生成 count-1 个随机数
        for (int i = 0; i < count - 1; i++) {
            double min = before + avg * (1 - lowFuDong);
            double max = before + avg * (1 + highFuDong);
            min = min > total ? before : min;
            max = max > total ? total : max;
            int randomEle = max > min ? (int) Math.round(RandomUtil.randomDouble(min, max)) : total;
            randomList.add(randomEle - before);
            before = randomEle;
        }
        randomList.add(total - before);
        if (flag) {
            return randomList.stream().map(ele -> ele + 1).collect(Collectors.toList());
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
     * 计算某天的权重
     *
     * @param weightMap 权重配置
     * @param day       天
     * @return
     */
    public static WeightRandom.WeightObj<DateTime> dayWeight(Map<String, Double> weightMap, DateTime day) {
        String monthKey = "1-" + (DateUtil.month(day) + 1);
        // 周日-周六：1 - 7
        String weekKey = "2-" + (DateUtil.dayOfWeek(day));
        String dayKey = "3-" + (DateUtil.dayOfMonth(day));
        double monthWeight = weightMap.get(monthKey) == null ? 1.0 : weightMap.get(monthKey);
        double weekWeight = weightMap.get(weekKey) == null ? 1.0 : weightMap.get(weekKey);
        double dayWeight = weightMap.get(dayKey) == null ? 1.0 : weightMap.get(dayKey);
        double weight = monthWeight * weekWeight * dayWeight;
        return new WeightRandom.WeightObj<>(day, weight);

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
        //Map<DateTime, TotalModel> dayTotalMap = biuldTotalModelFromWeight(dayWeightList, tjHisQuota, 0.3);
        Map<DateTime, TotalModel> dayTotalMap = new HashMap<>();
        dayWeightList.forEach(day -> {
            TotalModel totalModel = new TotalModel();
            totalModel.setOrgId(tjHisQuota.getOrgId()).setOrgName(tjHisQuota.getOrgName()).setDate(day.getObj());
            dayTotalMap.put(day.getObj(), totalModel);
        });
        int pvTotal = tjHisQuota.getPvCount();
        int scTotal = tjHisQuota.getScCount();
        int dcTotal = tjHisQuota.getDcCount();
        int ddcTotal = tjHisQuota.getDdcCount();
        int uvTotal = tjHisQuota.getUvCount();
        int vvTotal = tjHisQuota.getVvCount();
        // 计算用户访问总时间 = 平均访问时间 * 访问次数
        long avgTimeTotal = DateUtil.getTimeMillis(tjHisQuota.getAvgTime()) * vvTotal;
        // 随机生成：size为访问次数且总和等于总时间的随机列表
        List<Long> avgTimeList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, pvTotal);

        // 下载量
        List<Integer> dcMap = randomIntListFromFinalTotal(dcTotal, dayWeightList.size());
        List<Integer> ddcMap = randomIntListFromFinalTotal(ddcTotal, dayWeightList.size());
        Map<DateTime, Integer> scMap = randomListFromWeight(dayWeightList, scTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        scMap.forEach((k, v) -> {
            // 同时设置sc和pv量
            dayTotalMap.get(k).setScTotal(v)
                    .setPvTotal(v)
                    .setDcTotal(randomIntEle(dcMap, true).orElse(0))
                    .setDdcTotal(randomIntEle(ddcMap, true).orElse(0));
        });
        // uv和vv
        Map<DateTime, Integer> uvMap = randomListFromWeight(dayWeightList, uvTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        uvMap.forEach((k, v) -> {
            //同时设置uv和vv量
            dayTotalMap.get(k).setUvTotal(v).setVvTotal(v);
        });
        int svvTotal = vvTotal - uvTotal;
        Map<DateTime, Integer> vvMap = randomListFromWeight(dayWeightList, svvTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        vvMap.forEach((k, v) -> {
            // 在已有vv量基础上加上新的量
            int yvv = dayTotalMap.get(k).getVvTotal();
            int sumVv = yvv + v;
            dayTotalMap.get(k).setVvTotal(sumVv);
        });

        // 剩余pv
        int spvTotal = pvTotal - scTotal;
        Map<DateTime, Integer> pvMap = randomListFromWeight(dayWeightList, spvTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        pvMap.forEach((k, v) -> {
            // 在已有PV量基础上加上新的量
            int ypv = dayTotalMap.get(k).getPvTotal();
            int pv = ypv + v;
            List<Long> visitList = randomLongEles(avgTimeList, pv, true).orElse(new ArrayList<>());
            dayTotalMap.get(k).setPvTotal(pv)
                    .setVisitTimeTotal(visitList.stream().reduce((a, b) -> a + b).orElse(0L));
        });

        return dayTotalMap;
    }


    /**
     * @param beginTime     开始时间必须是从yyyy-MM-dd 00:00:00
     * @param endTime       结束时间必须是yyyy-MM-dd 23:59:00
     * @param dayTotalModel
     * @return
     */
    public static List<AbstractTjDataEntity> buildMinuteTjData(DateTime beginTime, DateTime endTime, Map.Entry<DateTime, TotalModel> dayTotalModel, boolean isHistory) {
        List<AbstractTjDataEntity> tjDayDataList = new ArrayList<>();
        // 如果是历史数据，按小时生成，否则按分钟生成
        DateField dateField = isHistory ? DateField.HOUR : DateField.MINUTE;
        //高峰时段分钟数列表
        List<DateTime> amHighMinutes = amHighMinutes(beginTime, endTime, dayTotalModel.getKey(), dateField);
        List<DateTime> pmHighMinutes = pmHighMinutes(beginTime, endTime, dayTotalModel.getKey(), dateField);
//        int highMinuteAllSize = 11 * 60;
//        double highSizeBl = 1.0 * highMinutes.size()/highMinuteAllSize;
        //低谷时段分钟数列表
        List<DateTime> amLowMinutes = amLowMinutes(beginTime, endTime, dayTotalModel.getKey(), dateField);
        List<DateTime> pmLowMinutes = pmLowMinutes(beginTime, endTime, dayTotalModel.getKey(), dateField);
//        int lowMinuteAllSize = 9 * 60;
//        double lowSizeBl = 1.0 * lowMinutes.size()/lowMinuteAllSize;
        //其它时段分钟数列表
        List<DateTime> otherMinutes = otherMinutes(beginTime, endTime, dayTotalModel.getKey(), dateField);
//        int otherMinuteAllSize = 4 * 60;
//        double otherSizeBl = 1.0 * otherMinutes.size()/otherMinuteAllSize;

        //高峰时段所占比列
        double amHighWeight = RandomUtil.randomDouble(0.32, 0.38);
        double pmHighWeight = RandomUtil.randomDouble(0.35, 0.40);
        //低谷时段所占比列
        double amLowWeight = RandomUtil.randomDouble(0.03, 0.1);
        double pmLowWeight = RandomUtil.randomDouble(0.1, 1 - amHighWeight - pmHighWeight - amLowWeight);
        //其它时段所占比列
        double otherWeight = 1 - amHighWeight - pmHighWeight - amLowWeight - pmLowWeight;

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

        DayXq dayScXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, scTotal).invoke();
        List<Integer> amHighScCountList = dayScXq.getAmHighCountList();
        List<Integer> pmHighScCountList = dayScXq.getPmHighCountList();
        List<Integer> amLowScCountList = dayScXq.getAmLowCountList();
        List<Integer> pmLowScCountList = dayScXq.getPmLowCountList();
        List<Integer> otherScCountList = dayScXq.getOtherCountList();

        //-----------------------pv
        int spvTotal = pvTotal - scTotal;
        DayXq daySpvXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, spvTotal).invoke();
        List<Integer> amHighSpvCountList = daySpvXq.getAmHighCountList();
        List<Integer> pmHighSpvCountList = daySpvXq.getPmHighCountList();
        List<Integer> amLowSpvCountList = daySpvXq.getAmLowCountList();
        List<Integer> pmLowSpvCountList = daySpvXq.getPmLowCountList();
        List<Integer> otherSpvCountList = daySpvXq.getOtherCountList();

        //-----------------------dc
        DayXq dayDcXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, dcTotal).invoke();
        List<Integer> amHighDcCountList = dayDcXq.getAmHighCountList();
        List<Integer> pmHighDcCountList = dayDcXq.getPmHighCountList();
        List<Integer> amLowDcCountList = dayDcXq.getAmLowCountList();
        List<Integer> pmLowDcCountList = dayDcXq.getPmLowCountList();
        List<Integer> otherDcCountList = dayDcXq.getOtherCountList();
        //-----------------------ddc
        DayXq dayDdcXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, ddcTotal).invoke();
        List<Integer> amHighDdcCountList = dayDdcXq.getAmHighCountList();
        List<Integer> pmHighDdcCountList = dayDdcXq.getPmHighCountList();
        List<Integer> amLowDdcCountList = dayDdcXq.getAmLowCountList();
        List<Integer> pmLowDdcCountList = dayDdcXq.getPmLowCountList();
        List<Integer> otherDdcCountList = dayDdcXq.getOtherCountList();
        //-----------------------uv
        DayXq dayUvXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, uvTotal).invoke();
        List<Integer> amHighUvCountList = dayUvXq.getAmHighCountList();
        List<Integer> pmHighUvCountList = dayUvXq.getPmHighCountList();
        List<Integer> amLowUvCountList = dayUvXq.getAmLowCountList();
        List<Integer> pmLowUvCountList = dayUvXq.getPmLowCountList();
        List<Integer> otherUvCountList = dayUvXq.getOtherCountList();
        //-----------------------vv
        int svvTotal = vvTotal - uvTotal;
        DayXq daySvvXq = new DayXq(amHighMinutes, pmHighMinutes, amLowMinutes, pmLowMinutes, otherMinutes, amHighWeight, pmHighWeight, amLowWeight, pmLowWeight, svvTotal).invoke();
        List<Integer> amHighSvvCountList = daySvvXq.getAmHighCountList();
        List<Integer> pmHighSvvCountList = daySvvXq.getPmHighCountList();
        List<Integer> amLowSvvCountList = daySvvXq.getAmLowCountList();
        List<Integer> pmLowSvvCountList = daySvvXq.getPmLowCountList();
        List<Integer> otherSvvCountList = daySvvXq.getOtherCountList();

        amHighMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(isHistory, orgName, tjDataPk, visitList, amHighScCountList, amHighSpvCountList, amHighDcCountList, amHighDdcCountList, amHighUvCountList, amHighSvvCountList));
        });
        pmHighMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(isHistory, orgName, tjDataPk, visitList, pmHighScCountList, pmHighSpvCountList, pmHighDcCountList, pmHighDdcCountList, pmHighUvCountList, pmHighSvvCountList));
        });
        amLowMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(isHistory, orgName, tjDataPk, visitList, amLowScCountList, amLowSpvCountList, amLowDcCountList, amLowDdcCountList, amLowUvCountList, amLowSvvCountList));
        });
        pmLowMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(isHistory, orgName, tjDataPk, visitList, pmLowScCountList, pmLowSpvCountList, pmLowDcCountList, pmLowDdcCountList, pmLowUvCountList, pmLowSvvCountList));
        });
        otherMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(isHistory, orgName, tjDataPk, visitList, otherScCountList, otherSpvCountList, otherDcCountList, otherDdcCountList, otherUvCountList, otherSvvCountList));
        });
        return tjDayDataList;
    }

    /**
     * 创建tjData对象
     *
     * @param orgName
     * @param tjDataPk
     * @param visitList
     * @param scCountList
     * @param spvCountList
     * @param dcCountList
     * @param ddcCountList
     * @param uvCountList
     * @param svvCountList
     * @return
     */
    private static AbstractTjDataEntity createTjData(boolean isHistory, String orgName, TjDataPk tjDataPk, List<Long> visitList, List<Integer> scCountList, List<Integer> spvCountList, List<Integer> dcCountList, List<Integer> ddcCountList, List<Integer> uvCountList, List<Integer> svvCountList) {
        AbstractTjDataEntity tjData;
        if (isHistory) {
            tjData = new TjViewData();
        } else {
            tjData = new TjTaskData();
        }
        int scCount = RandomUtil.randomIntEle(scCountList, true).orElse(0);
        int spvCount = RandomUtil.randomIntEle(spvCountList, true).orElse(0);
        int dcCount = RandomUtil.randomIntEle(dcCountList, true).orElse(0);
        int ddcCount = RandomUtil.randomIntEle(ddcCountList, true).orElse(0);
        int uvCount = RandomUtil.randomIntEle(uvCountList, true).orElse(0);
        int svvCount = RandomUtil.randomIntEle(svvCountList, true).orElse(0);
        List<Long> visites = RandomUtil.randomLongEles(visitList, uvCount + svvCount, true).orElse(new ArrayList<>());
        long visit = visites.stream().reduce((a, b) -> a + b).orElse(0L);
        tjData.setScCount(scCount)
                .setPvCount(scCount + spvCount)
                .setDcCount(dcCount)
                .setDdcCount(ddcCount)
                .setUvCount(uvCount)
                .setVvCount(uvCount + svvCount)
                .setVisitTime(visit)
                .setId(tjDataPk)
                .setOrgName(orgName);
        return tjData;
    }


    /**
     * 8点至12点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> amHighMinutes(DateTime beginTime, DateTime endTime, DateTime day, DateField dateField) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 8).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 11).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在12点之后或结束时间在8点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }

        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, dateField);
    }

    /**
     * 14点至18点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> pmHighMinutes(DateTime beginTime, DateTime endTime, DateTime day, DateField dateField) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 14).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 18).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在18点之后或结束时间在14点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }

        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, dateField);
    }

    /**
     * 12点至14点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> amLowMinutes(DateTime beginTime, DateTime endTime, DateTime day, DateField dateField) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 12).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 13).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在22点之后或结束时间在19点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }
        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, dateField);
    }

    /**
     * 19点至22点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> pmLowMinutes(DateTime beginTime, DateTime endTime, DateTime day, DateField dateField) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 19).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 21).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在22点之后或结束时间在19点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }
        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, dateField);
    }


    /**
     * 低谷分钟数列表
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> otherMinutes(DateTime beginTime, DateTime endTime, DateTime day, DateField dateField) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 7).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);

        DateTime beginMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 22).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        List<DateTime> otherMinutes = new ArrayList<>();
        //开始时间在当天的7点之前 且 结束时间在当天的0点之后
        if (beginTime.before(endMinute) && endTime.after(beginMinute)) {
            beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
            endMinute = endTime.before(endMinute) ? endTime : endMinute;
            otherMinutes = DateUtil.rangeToList(beginMinute, endMinute, dateField);
        }
        //开始时间在当天的24点之前 且 结束时间在当天的23点之后
        if (beginTime.before(endMinute2) && endTime.after(beginMinute2)) {
            beginMinute2 = beginTime.after(beginMinute2) ? beginTime : beginMinute2;
            endMinute2 = endTime.before(endMinute2) ? endTime : endMinute2;
            otherMinutes.addAll(DateUtil.rangeToList(beginMinute2, endMinute2, dateField));
        }
        return otherMinutes;
    }


    private static class DayXq {
        private List<DateTime> amHighMinutes;
        private List<DateTime> pmHighMinutes;
        private List<DateTime> amLowMinutes;
        private List<DateTime> pmLowMinutes;
        private List<DateTime> otherMinutes;
        private double amHighWeight;
        private double pmHighWeight;
        private double amLowWeight;
        private double pmLowWeight;
        private int total;
        private List<Integer> amHighCountList;
        private List<Integer> pmHighCountList;
        private List<Integer> amLowCountList;
        private List<Integer> pmLowCountList;
        private List<Integer> otherCountList;

        public DayXq(List<DateTime> amHighMinutes, List<DateTime> pmHighMinutes, List<DateTime> amLowMinutes, List<DateTime> pmLowMinutes, List<DateTime> otherMinutes, double amHighWeight, double pmHighWeight, double amLowWeight, double pmLowWeight, int dcTotal) {
            this.amHighMinutes = amHighMinutes;
            this.pmHighMinutes = pmHighMinutes;
            this.amLowMinutes = amLowMinutes;
            this.pmLowMinutes = pmLowMinutes;
            this.otherMinutes = otherMinutes;
            this.amHighWeight = amHighWeight;
            this.pmHighWeight = pmHighWeight;
            this.amLowWeight = amLowWeight;
            this.pmLowWeight = pmLowWeight;
            this.total = dcTotal;
        }

        public List<Integer> getAmHighCountList() {
            return amHighCountList;
        }

        public List<Integer> getPmHighCountList() {
            return pmHighCountList;
        }

        public List<Integer> getAmLowCountList() {
            return amLowCountList;
        }


        public List<Integer> getPmLowCountList() {
            return pmLowCountList;
        }

        public List<Integer> getOtherCountList() {
            return otherCountList;
        }


        public DayXq invoke() {
            int amHighTotal = (int) Math.round(amHighWeight * total);
            int pmHighTotal = (int) Math.round(pmHighWeight * total);
            int amLowTotal = (int) Math.round(amLowWeight * total);
            int pmLowTotal = (int) Math.round(pmLowWeight * total);
            int otherTotal = total - amHighTotal - pmHighTotal - amLowTotal - pmLowTotal;
            amHighCountList = RandomUtil.randomIntListFromFinalTotal(amHighTotal, amHighMinutes.size());
            pmHighCountList = RandomUtil.randomIntListFromFinalTotal(pmHighTotal, pmHighMinutes.size());
            amLowCountList = RandomUtil.randomIntListFromFinalTotal(amLowTotal, amLowMinutes.size());
            pmLowCountList = RandomUtil.randomIntListFromFinalTotal(pmLowTotal, pmLowMinutes.size());
            otherCountList = RandomUtil.randomIntListFromFinalTotal(otherTotal, otherMinutes.size());
            return this;
        }
    }
}

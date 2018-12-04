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
        List<Long> avgTimeList = RandomUtil.randomLongListFromFinalTotal(avgTimeTotal, vvTotal);


        Map<DateTime, Integer> scMap = randomListFromWeight(dayWeightList, scTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        scMap.forEach((k, v) -> {
            // 同时设置sc和pv量
            dayTotalMap.get(k).setScTotal(v).setPvTotal(v);
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
            List<Long> visitTimeList = RandomUtil.randomLongEles(avgTimeList, sumVv, true).orElse(new ArrayList<>());
            dayTotalMap.get(k).setVisitTimeTotal(visitTimeList.stream().reduce((a, b) -> a + b).orElse(0L));
        });

        // 剩余pv
        int spvTotal = pvTotal - scTotal;
        Map<DateTime, Integer> pvMap = randomListFromWeight(dayWeightList, spvTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        pvMap.forEach((k, v) -> {
            // 在已有PV量基础上加上新的量
            int ypv = dayTotalMap.get(k).getPvTotal();
            dayTotalMap.get(k).setPvTotal(ypv + v);
        });
        // 下载量
        Map<DateTime, Integer> dcMap = randomListFromWeight(dayWeightList, dcTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        dcMap.forEach((k, v) -> {
            dayTotalMap.get(k).setDcTotal(v);
        });
        // 文献传递量
        Map<DateTime, Integer> ddcMap = randomListFromWeight(dayWeightList, ddcTotal, RandomUtil.randomDouble(LOW_FU_DONG, HIGH_FU_DONG));
        ddcMap.forEach((k, v) -> {
            dayTotalMap.get(k).setDdcTotal(v);
        });

        return dayTotalMap;
    }


    /**
     * @param beginTime     开始时间必须是从yyyy-MM-dd 00:00:00
     * @param endTime       结束时间必须是yyyy-MM-dd 23:59:00
     * @param dayTotalModel
     * @return
     */
    public static List<AbstractTjDataEntity> buildMinuteTjData(DateTime beginTime, DateTime endTime, Map.Entry<DateTime, TotalModel> dayTotalModel, Class clazz) {
        List<AbstractTjDataEntity> tjDayDataList = new ArrayList<>();
        //高峰时段分钟数列表
        List<DateTime> amHighMinutes = getAmHighMinutes(beginTime, endTime, dayTotalModel.getKey());
        List<DateTime> pmHighMinutes = getPmHighMinutes(beginTime, endTime, dayTotalModel.getKey());
//        int highMinuteAllSize = 11 * 60;
//        double highSizeBl = 1.0 * highMinutes.size()/highMinuteAllSize;
        //低谷时段分钟数列表
        List<DateTime> amLowMinutes = getAmLowMinutes(beginTime, endTime, dayTotalModel.getKey());
        List<DateTime> pmLowMinutes = getPmLowMinutes(beginTime, endTime, dayTotalModel.getKey());
//        int lowMinuteAllSize = 9 * 60;
//        double lowSizeBl = 1.0 * lowMinutes.size()/lowMinuteAllSize;
        //其它时段分钟数列表
        List<DateTime> otherMinutes = getOtherMinutes(beginTime, endTime, dayTotalModel.getKey());
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
        int amHighScTotal = (int) Math.round(amHighWeight * scTotal);
        int pmHighScTotal = (int) Math.round(pmHighWeight * scTotal);
        int amLowScTotal = (int) Math.round(amLowWeight * scTotal);
        int pmLowScTotal = (int) Math.round(pmLowWeight * scTotal);
        int otherScTotal = scTotal - amHighScTotal - pmHighScTotal - amLowScTotal - pmLowScTotal;
        List<Integer> amHighScCountList = RandomUtil.randomIntListFromFinalTotal(amHighScTotal, amHighMinutes.size());
        List<Integer> pmHighScCountList = RandomUtil.randomIntListFromFinalTotal(pmHighScTotal, pmHighMinutes.size());
        List<Integer> amLowScCountList = RandomUtil.randomIntListFromFinalTotal(amLowScTotal, amLowMinutes.size());
        List<Integer> pmLowScCountList = RandomUtil.randomIntListFromFinalTotal(pmLowScTotal, pmLowMinutes.size());
        List<Integer> otherScCountList = RandomUtil.randomIntListFromFinalTotal(otherScTotal, otherMinutes.size());

        //-----------------------pv
        int spvTotal = pvTotal - scTotal;
        int amHighSpvTotal = (int) Math.round(amHighWeight * spvTotal);
        int pmHighSpvTotal = (int) Math.round(pmHighWeight * spvTotal);
        int amLowSpvTotal = (int) Math.round(amLowWeight * spvTotal);
        int pmLowSpvTotal = (int) Math.round(pmLowWeight * spvTotal);
        int otherSpvTotal = spvTotal - amHighSpvTotal - pmHighSpvTotal - amLowSpvTotal - pmLowSpvTotal;

        List<Integer> amHighSpvCountList = RandomUtil.randomIntListFromFinalTotal(amHighSpvTotal, amHighMinutes.size());
        List<Integer> pmHighSpvCountList = RandomUtil.randomIntListFromFinalTotal(pmHighSpvTotal, pmHighMinutes.size());
        List<Integer> amLowSpvCountList = RandomUtil.randomIntListFromFinalTotal(amLowSpvTotal, amLowMinutes.size());
        List<Integer> pmLowSpvCountList = RandomUtil.randomIntListFromFinalTotal(pmLowSpvTotal, pmLowMinutes.size());
        List<Integer> otherSpvCountList = RandomUtil.randomIntListFromFinalTotal(otherSpvTotal, otherMinutes.size());

        //-----------------------dc
        int amHighDcTotal = (int) Math.round(amHighWeight * dcTotal);
        int pmHighDcTotal = (int) Math.round(pmHighWeight * dcTotal);
        int amLowDcTotal = (int) Math.round(amLowWeight * dcTotal);
        int pmLowDcTotal = (int) Math.round(pmLowWeight * dcTotal);
        int otherDcTotal = dcTotal - amHighDcTotal - pmHighDcTotal - amLowDcTotal - pmLowDcTotal;
        List<Integer> amHighDcCountList = RandomUtil.randomIntListFromFinalTotal(amHighDcTotal, amHighMinutes.size());
        List<Integer> pmHighDcCountList = RandomUtil.randomIntListFromFinalTotal(pmHighDcTotal, pmHighMinutes.size());
        List<Integer> amLowDcCountList = RandomUtil.randomIntListFromFinalTotal(amLowDcTotal, amLowMinutes.size());
        List<Integer> pmLowDcCountList = RandomUtil.randomIntListFromFinalTotal(pmLowDcTotal, pmLowMinutes.size());
        List<Integer> otherDcCountList = RandomUtil.randomIntListFromFinalTotal(otherDcTotal, otherMinutes.size());
        //-----------------------ddc
        int amHighDdcTotal = (int) Math.round(amHighWeight * ddcTotal);
        int pmHighDdcTotal = (int) Math.round(pmHighWeight * ddcTotal);
        int amLowDdcTotal = (int) Math.round(amLowWeight * ddcTotal);
        int pmLowDdcTotal = (int) Math.round(pmLowWeight * ddcTotal);
        int otherDdcTotal = ddcTotal - amHighDdcTotal - pmHighDdcTotal - amLowDdcTotal - pmLowDdcTotal;
        List<Integer> amHighDdcCountList = RandomUtil.randomIntListFromFinalTotal(amHighDdcTotal, amHighMinutes.size());
        List<Integer> pmHighDdcCountList = RandomUtil.randomIntListFromFinalTotal(pmHighDdcTotal, pmHighMinutes.size());
        List<Integer> amLowDdcCountList = RandomUtil.randomIntListFromFinalTotal(amLowDdcTotal, amLowMinutes.size());
        List<Integer> pmLowDdcCountList = RandomUtil.randomIntListFromFinalTotal(pmLowDdcTotal, pmLowMinutes.size());
        List<Integer> otherDdcCountList = RandomUtil.randomIntListFromFinalTotal(otherDdcTotal, otherMinutes.size());
        //-----------------------uv
        int amHighUvTotal = (int) Math.round(amHighWeight * uvTotal);
        int pmHighUvTotal = (int) Math.round(pmHighWeight * uvTotal);
        int amLowUvTotal = (int) Math.round(amLowWeight * uvTotal);
        int pmLowUvTotal = (int) Math.round(pmLowWeight * uvTotal);
        int otherUvTotal = uvTotal - amHighUvTotal - pmHighUvTotal - amLowUvTotal - pmLowUvTotal;
        List<Integer> amHighUvCountList = RandomUtil.randomIntListFromFinalTotal(amHighUvTotal, amHighMinutes.size());
        List<Integer> pmHighUvCountList = RandomUtil.randomIntListFromFinalTotal(pmHighUvTotal, pmHighMinutes.size());
        List<Integer> amLowUvCountList = RandomUtil.randomIntListFromFinalTotal(amLowUvTotal, amLowMinutes.size());
        List<Integer> pmLowUvCountList = RandomUtil.randomIntListFromFinalTotal(pmLowUvTotal, pmLowMinutes.size());
        List<Integer> otherUvCountList = RandomUtil.randomIntListFromFinalTotal(otherUvTotal, otherMinutes.size());
        //-----------------------vv
        int svvTotal = vvTotal - uvTotal;
        int amHighSvvTotal = (int) Math.round(amHighWeight * svvTotal);
        int pmHighSvvTotal = (int) Math.round(pmHighWeight * svvTotal);
        int amLowSvvTotal = (int) Math.round(amLowWeight * svvTotal);
        int pmLowSvvTotal = (int) Math.round(pmLowWeight * svvTotal);
        int otherSvvTotal = svvTotal - amHighSvvTotal - pmHighSvvTotal - amLowSvvTotal - pmLowSvvTotal;
        List<Integer> amHighSvvCountList = RandomUtil.randomIntListFromFinalTotal(amHighSvvTotal, amHighMinutes.size());
        List<Integer> pmHighSvvCountList = RandomUtil.randomIntListFromFinalTotal(pmHighSvvTotal, pmHighMinutes.size());
        List<Integer> amLowSvvCountList = RandomUtil.randomIntListFromFinalTotal(amLowSvvTotal, amLowMinutes.size());
        List<Integer> pmLowSvvCountList = RandomUtil.randomIntListFromFinalTotal(pmLowSvvTotal, pmLowMinutes.size());
        List<Integer> otherSvvCountList = RandomUtil.randomIntListFromFinalTotal(otherSvvTotal, otherMinutes.size());

        amHighMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(clazz, orgName, tjDataPk, visitList, amHighScCountList, amHighSpvCountList, amHighDcCountList, amHighDdcCountList, amHighUvCountList, amHighSvvCountList));
        });
        pmHighMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(clazz, orgName, tjDataPk, visitList, pmHighScCountList, pmHighSpvCountList, pmHighDcCountList, pmHighDdcCountList, pmHighUvCountList, pmHighSvvCountList));
        });
        amLowMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(clazz, orgName, tjDataPk, visitList, amLowScCountList, amLowSpvCountList, amLowDcCountList, amLowDdcCountList, amLowUvCountList, amLowSvvCountList));
        });
        pmLowMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(clazz, orgName, tjDataPk, visitList, pmLowScCountList, pmLowSpvCountList, pmLowDcCountList, pmLowDdcCountList, pmLowUvCountList, pmLowSvvCountList));
        });
        otherMinutes.forEach(minute -> {
            TjDataPk tjDataPk = new TjDataPk(orgId, minute);
            tjDayDataList.add(createTjData(clazz, orgName, tjDataPk, visitList, otherScCountList, otherSpvCountList, otherDcCountList, otherDdcCountList, otherUvCountList, otherSvvCountList));
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
    private static AbstractTjDataEntity createTjData(Class clazz, String orgName, TjDataPk tjDataPk, List<Long> visitList, List<Integer> highScCountList, List<Integer> highSpvCountList, List<Integer> highDcCountList, List<Integer> highDdcCountList, List<Integer> highUvCountList, List<Integer> highSvvCountList) {
        AbstractTjDataEntity tjData;
        if (clazz.equals(TjViewData.class)) {
            tjData = new TjViewData();
        } else if (clazz.equals(TjTaskData.class)) {
            tjData = new TjTaskData();
        } else {
            tjData = new TjSpisData();
        }
        int scCount = RandomUtil.randomIntEle(highScCountList, true).orElse(0);
        int spvCount = RandomUtil.randomIntEle(highSpvCountList, true).orElse(0);
        int dcCount = RandomUtil.randomIntEle(highDcCountList, true).orElse(0);
        int ddcCount = RandomUtil.randomIntEle(highDdcCountList, true).orElse(0);
        int uvCount = RandomUtil.randomIntEle(highUvCountList, true).orElse(0);
        int svvCount = RandomUtil.randomIntEle(highSvvCountList, true).orElse(0);
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
    private static List<DateTime> getAmHighMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 8).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 11).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在12点之后或结束时间在8点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }

        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.HOUR);
    }

    /**
     * 14点至18点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getPmHighMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 14).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 18).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在18点之后或结束时间在14点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }

        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.HOUR);
    }

    /**
     * 12点至14点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getAmLowMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 12).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 13).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在22点之后或结束时间在19点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }
        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.HOUR);
    }

    /**
     * 19点至22点
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getPmLowMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 19).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 21).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        // 如果开始时间在22点之后或结束时间在19点之前，直接返回空
        if (beginTime.after(endMinute) || endTime.before(beginMinute)) {
            return new ArrayList<>();
        }
        beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
        endMinute = endTime.before(endMinute) ? endTime : endMinute;
        return DateUtil.rangeToList(beginMinute, endMinute, DateField.HOUR);
    }


    /**
     * 低谷分钟数列表
     *
     * @param beginTime
     * @param endTime
     * @param day
     * @return
     */
    private static List<DateTime> getOtherMinutes(DateTime beginTime, DateTime endTime, DateTime day) {
        DateTime beginMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 7).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);

        DateTime beginMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 22).setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0);
        DateTime endMinute2 = day.setMutable(false).setField(DateField.HOUR_OF_DAY, 23).setField(DateField.MINUTE, 59).setField(DateField.SECOND, 59);
        List<DateTime> otherMinutes = new ArrayList<>();
        //开始时间在当天的7点之前 且 结束时间在当天的0点之后
        if (beginTime.before(endMinute) && endTime.after(beginMinute)) {
            beginMinute = beginTime.after(beginMinute) ? beginTime : beginMinute;
            endMinute = endTime.before(endMinute) ? endTime : endMinute;
            otherMinutes = DateUtil.rangeToList(beginMinute, endMinute, DateField.HOUR);
        }
        //开始时间在当天的24点之前 且 结束时间在当天的23点之后
        if (beginTime.before(endMinute2) && endTime.after(beginMinute2)) {
            beginMinute2 = beginTime.after(beginMinute2) ? beginTime : beginMinute2;
            endMinute2 = endTime.before(endMinute2) ? endTime : endMinute2;
            otherMinutes.addAll(DateUtil.rangeToList(beginMinute2, endMinute2, DateField.HOUR));
        }
        return otherMinutes;
    }


}

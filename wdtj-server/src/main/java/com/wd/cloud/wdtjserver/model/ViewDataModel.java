package com.wd.cloud.wdtjserver.model;

import com.wd.cloud.wdtjserver.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 前台展示数据模型
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "返回给前台数据对象")
public class ViewDataModel {

    private String orgFlag;

    private List<String> tjDate = new ArrayList<>();

    private Integer pvTotal;

    private List<Integer> pvCount = new ArrayList<>();

    private Integer scTotal;

    private List<Integer> scCount = new ArrayList<>();

    private Integer dcTotal;

    private List<Integer> dcCount = new ArrayList<>();

    private Integer ddcTotal;

    private List<Integer> ddcCount = new ArrayList<>();

    private Integer uvTotal;

    private List<Integer> uvCount = new ArrayList<>();

    private Integer vvTotal;

    private List<Integer> vvCount = new ArrayList<>();

    private Long avgTimeTotal;

    private List<Long> avgTime = new ArrayList<>();


    /**
     * 计算总量
     *
     * @return
     */
    public void sumTotal(long sumVisitTime) {
        this.setPvTotal(this.pvCount.stream().reduce((a, b) -> a + b).orElse(0));
        this.setScTotal(this.scCount.stream().reduce((a, b) -> a + b).orElse(0));
        this.setDcTotal(this.dcCount.stream().reduce((a, b) -> a + b).orElse(0));
        this.setDdcTotal(this.ddcCount.stream().reduce((a, b) -> a + b).orElse(0));
        this.setUvTotal(this.uvCount.stream().reduce((a, b) -> a + b).orElse(0));
        this.setVvTotal(this.vvCount.stream().reduce((a, b) -> a + b).orElse(0));
        long avgTotal = this.vvTotal == 0 ? 0 : sumVisitTime / this.vvTotal;
        System.out.println("平均访问时长：" + avgTotal + ",format：" + DateUtil.createTime(avgTotal).toString());
        this.setAvgTimeTotal(avgTotal);
    }
}

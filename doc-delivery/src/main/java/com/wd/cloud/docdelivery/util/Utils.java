package com.wd.cloud.docdelivery.util;

import com.wd.cloud.docdelivery.pojo.entity.LiteraturePlan;
import com.wd.cloud.docdelivery.service.LiteraturePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 排班人员工具类
 */
@Component
public class Utils {

    @Autowired
    private  static LiteraturePlanService literaturePlanService;

    /**
     * 排班日期
     */
    private static String arrangeDate;

    /**
     * 格式转换器
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");


    /**
     * 当天排班人员
     */
    private static List<LiteraturePlan> userNames;

    /**
     * 当前排的位置
     */
     private static int place;

    /**
     * 返回排班人员
     * @return
     */
    public static LiteraturePlan getUserName() {
        //获取当天日期,查询排班人员
        String nowDate = sdf.format(new Date());
        //如果日期大于当前日期或者排班日期为空,则默认代替
        if(arrangeDate == null || nowDate.compareTo(arrangeDate) > 0) {
            //查询排班人员并且赋值返回
            userNames = literaturePlanService.findByDate();
            //将位置置为0,日期进行设值
            arrangeDate = nowDate;
            place = 0;
        };
        //设置返回位置的人员
        int temp = place;
        //如果排班位置已经到最后一个,继续从0开始
        if(++place > userNames.size()) {
            temp = 0;
            place = 1;
        }
        //如果当天没排班,默认返回空
        if(userNames.size() == 0) {
            System.out.println("返回了空数据********");
            return null;
        }
        return userNames.get(temp);
    }


    @Autowired(required = true)
    public  void setDocImageFileDao(LiteraturePlanService literaturePlanService) {
        Utils.literaturePlanService = literaturePlanService;
    }
    @Autowired(required = true)
    public  void setImageFileDao(LiteraturePlanService literaturePlanService) {
        Utils.literaturePlanService = literaturePlanService;
    }


}

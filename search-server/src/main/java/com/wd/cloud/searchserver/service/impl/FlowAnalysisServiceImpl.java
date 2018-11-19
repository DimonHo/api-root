package com.wd.cloud.searchserver.service.impl;
import cn.hutool.db.sql.SqlUtil;
import com.wd.cloud.searchserver.service.BrowseSearchI;
import com.wd.cloud.searchserver.service.FlowAnalysisServiceI;
import com.wd.cloud.searchserver.util.DateUtil;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author He Zhigang
 * @date 2018/11/12 0012
 * @Description:
 */
@Service("flowAnalysisService")
public class FlowAnalysisServiceImpl implements FlowAnalysisServiceI {

    @Autowired
    private BrowseSearchI browseSearch;

    @Override
    public List<Map<String, Object>> visite(Long orgId, String tjDate) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String beginTime = df.format(tjDate);
        Calendar nowTime = Calendar.getInstance();
        Date date = DateUtil.toDate(tjDate);
        nowTime.setTime(date);
        nowTime.add(Calendar.MINUTE,1);
        date = nowTime.getTime();
        String endTime=df.format(date);
        Map<String, Object> map = browseSearch.indexInfo("", tjDate, endTime);
        list.add(map);
        return list;
    }
}

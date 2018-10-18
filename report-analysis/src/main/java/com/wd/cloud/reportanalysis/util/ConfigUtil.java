package com.wd.cloud.reportanalysis.util;


import cn.hutool.setting.Setting;
import java.util.Iterator;

/**
 * @author yangshuaifei
 * @date 2018/9/14
 * @Description:
 */
public class ConfigUtil {

    static Setting setting = new Setting("config.setting",true);

    
    Iterator<Setting.Entry<String,String>> it=setting.entrySet().iterator();

    public static String getStr(String key) {
            return setting.getStr(key);
    }
    
    public static Iterator getIterator() {
    	Iterator<Setting.Entry<String,String>> it=setting.entrySet().iterator();
    	return it;
    }
    
    
}

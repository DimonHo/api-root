package org.bse.server.util;


import cn.hutool.setting.Setting;

import java.util.Iterator;

/**
 * @author yangshuaifei
 * @date 2018/9/14
 * @Description:
 */
public class ConfigUtil {

    static Setting setting = new Setting("config.setting", true);

    public static String getStr(String key) {
        return setting.getStr(key);
    }
    
    
    
    
    static Setting facesSetting = new Setting("facet.setting", true);

    public static Iterator getFacesSettingIterator() {
        Iterator<Setting.Entry<String, String>> it = facesSetting.entrySet().iterator();
        return it;
    }


}

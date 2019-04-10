package com.wd.cloud.bse.util;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用代码的提取，如非空验证的代码
 *
 * @author Administrator
 */
public class SimpleUtil {

    private static final Set<String> orgFeaturesSet = new HashSet<String>();

    static {
        orgFeaturesSet.add("公司");
        orgFeaturesSet.add("集团");
        orgFeaturesSet.add("血站");
        orgFeaturesSet.add("中心");
        orgFeaturesSet.add("大学");
        orgFeaturesSet.add("学院");
        orgFeaturesSet.add("研究所");
        orgFeaturesSet.add("实验室");
        orgFeaturesSet.add("研究院");
        orgFeaturesSet.add("总局");
        orgFeaturesSet.add("委员会");
        // 报纸机构
        orgFeaturesSet.add("时报");
        orgFeaturesSet.add("日报");
        orgFeaturesSet.add("周报");
        orgFeaturesSet.add("晚报");
        orgFeaturesSet.add("晨报");
        // 图书机构
        orgFeaturesSet.add("出版社");
        //英文机构
        orgFeaturesSet.add("lab");
        orgFeaturesSet.add("inst");
        orgFeaturesSet.add("hosp");
        orgFeaturesSet.add("univ");
        orgFeaturesSet.add("dept");
    }

    public static final boolean isOrg(String _text) {
        boolean isOrg = false;
        String text = _text.toLowerCase();
        if (text.length() > 2) {
            for (String orgFeature : orgFeaturesSet) {
                if (text.contains(orgFeature)) {
                    isOrg = true;
                }
            }
        }
        return isOrg;
    }

    /**
     * 截取指定长度的文本
     *
     * @param text   原始文本内容
     * @param maxLen 允许的最大文本长度
     * @return
     */
    public static final String substring(String text, int maxLen) {
        String tmp = text.replaceAll("<font [^>]+>", "").replaceAll("</font>", "");
        if (tmp.length() <= maxLen) {
            return text;
        } else {
            tmp = tmp.substring(0, maxLen);
        }
        Pattern p = Pattern.compile("<font [^>]+>");
        Matcher m = p.matcher(text);
        List<Integer> startPosList = new ArrayList<Integer>();
        // 记录每个高亮标签开始的位置
        while (m.find()) {
            startPosList.add(m.start());
        }
        List<Integer> endPosList = new ArrayList<Integer>();
        // 记录每个高亮标签结束的位置
        p = Pattern.compile("</font>");
        m = p.matcher(text);
        while (m.find()) {
            endPosList.add(m.start());
        }
        StringBuilder stringBuilder = new StringBuilder(tmp);
        // 还原高亮标签
        for (int i = 0; i < startPosList.size(); i++) {
            if (startPosList.get(i) > tmp.length()) {
                break;
            }
            stringBuilder.insert(startPosList.get(i), "<font class='highlight'>");
            stringBuilder.insert(endPosList.get(i), "</font>");
        }
        return stringBuilder.toString();
    }

    /**
     * 获取关键词(keyword)在源句子(srcText)中出现的次数
     *
     * @param srcText
     * @param keyword 关键词
     * @return
     */
    public static int findStrCount(String srcText, String keyword) {
        int count = 0;
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public static boolean stringObjNotNull(Object str) {
        if (null == str) {
            return false;
        }
        if ("".equals(str.toString().trim())) {
            return false;
        }
        return true;
    }

    public static boolean strIsNull(String testStr) {

        if (null == testStr || "".equals(testStr.trim())) {
            return true;
        }
        return false;
    }

    public static boolean strNotNull(String testStr) {

        if (null == testStr || "".equals(testStr.trim())) {
            return false;
        }
        return true;
    }

    public static boolean collNotNull(Collection<?> testColl) {

        if (null == testColl || testColl.isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean collIsNull(Collection<?> testColl) {

        if (null == testColl || testColl.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean mapIsNull(Map<?, ?> testMap) {

        if (null == testMap || testMap.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean mapNotNull(Map<?, ?> testMap) {

        if (null == testMap || testMap.isEmpty()) {
            return false;
        }
        return true;
    }

    public static <T> boolean arrayNotNull(T[] testArray) {

        if (null == testArray || testArray.length == 0) {
            return false;
        }
        return true;
    }

    public static <T> boolean arrayIsNull(T[] testArray) {

        if (null == testArray || testArray.length == 0) {
            return true;
        }
        return false;
    }

    public static String symbolReplace(String content) {
        if (null != content) {
            content = content.replaceAll("\"", "&quot;");// 将引号替换成&quot;
        }
        return content;
    }
}

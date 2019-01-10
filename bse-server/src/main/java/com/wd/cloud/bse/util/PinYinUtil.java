package com.wd.cloud.bse.util;


import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	
	private static final Map<Character,String> POLYPHONE ;
	
	static{
		POLYPHONE = new HashMap<Character,String>();
		POLYPHONE.put('曾', "zeng");
		POLYPHONE.put('乐', "yue");
		POLYPHONE.put('仇', "qiu");
		POLYPHONE.put('单', "shan");
		POLYPHONE.put('解', "xie");
		POLYPHONE.put('区', "ou");
		POLYPHONE.put('盖', "ge");
		POLYPHONE.put('查', "zha");
	}

	/**
	 * 得到 全拼
	 * 
	 * @param src
	 * @return
	 */
	public static String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					if(i ==0 && POLYPHONE.get(t1[i]) != null){
						t4+=POLYPHONE.get(t1[i]);
					}else{
						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
						t4 += t2[0];
					}
				} else {
					t4 += java.lang.Character.toString(t1[i]);
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}
	
	public static String[] getPinyinNames(String name){
		char[] t1 = null;
		t1 = name.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String[] names = new String[3];
		String t4 = "",t5="",t6="",t7="",tmp;
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					if(i ==0 && POLYPHONE.get(t1[i]) != null){
						t2 = new String[]{POLYPHONE.get(t1[i])};
					}else{
						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					}
					tmp = t2[0];
				} else {
					tmp = java.lang.Character.toString(t1[i]);
				}
				if(i ==0 ){
					t5 = t4 = StringUtils.capitalize(tmp) + ", ";
					t7 = StringUtils.capitalize(tmp);
				}else{
					if(i==1){
						tmp =  StringUtils.capitalize(tmp);
					}
					t4 = t4+tmp;
					t5 = t5 + StringUtils.capitalize(tmp);
					t6 = t6 + StringUtils.capitalize(tmp);
				}
			}
			names[0] = t4;
			names[1] = t5;
			names[2] = t6 + ", "+t7;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return names;
	}

	
	public static String getPinyinName(String name){
		char[] t1 = null;
		t1 = name.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "",tmp;
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					if(i ==0 && POLYPHONE.get(t1[i]) != null){
						t2 = new String[]{POLYPHONE.get(t1[i])};
					}else{
						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					}
					tmp = t2[0];
				} else {
					tmp = java.lang.Character.toString(t1[i]);
				}
				if(i ==0 ){
					t4 = StringUtils.capitalize(tmp) + ", ";
				}else{
					if(i==1){
						tmp =  StringUtils.capitalize(tmp);
					}
					t4 = t4+tmp;
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}

	/**
	 * 得到中文首字母
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinYinHeadChar(String str) {

		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}
	
	/**
	 * 将字符串转移为ASCII码
	 * 
	 * @param cnStr
	 * @return
	 */
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}

}

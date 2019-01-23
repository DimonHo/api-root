package com.wd.cloud.bse.util;


import cn.hutool.core.util.StrUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.weidu.common.util.TextUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者比对
 *
 * @author Administrator
 */
public class AuthorUtil {


//	public static String isContinue(String authors,String author) {
//		String pinyings = PinYin4jUtil.getPinyinToLowerCase(author);
//		if(pinyings != null) {
//			List list = Arrays.asList(pinyings.split(";"));
//			author = isContinue(authors, author, list);
//		} else {
//			author = isContinue(authors, author, new ArrayList());
//		}
//		return author;
//	}


    public static Map<String, String> alignAuther(String author, Map<String, String> authorMap) {
        Map<String, String> map = alignAuther(author, authorMap.keySet());
        Map<String, String> rMap = new HashMap<>();
        for (Entry<String, String> entry : map.entrySet()) {
            rMap.put(entry.getValue(), authorMap.get(entry.getKey()));
        }
        return rMap;
    }

    public static Map<String, String> alignAuther(String author, Set<String> set) {
        Map<String, String> alignMap = new HashMap<>();
        String key;
        Set<String> set2 = new HashSet<>(set);
        Set<String> unMatchSet = new HashSet<>();
        if (StrUtil.isNotBlank(author)) {
            String[] authors = author.split(";");
            for (String name : authors) {
                key = containName(name, set2);
                if (key != null) {
                    set2.remove(key);
                    alignMap.put(key, name);
                    if (set2.size() == 0) {
                        break;
                    }
                } else {
                    unMatchSet.add(name);
                }
            }
        }
        return alignMap;
    }

    private static String containName(String name, Set<String> set) {
        String t = null;
        String nameLow = name.toLowerCase();
        if (set.contains(nameLow)) {
            return nameLow;
        }
        Set<String> tranNames = extendName(nameLow), tranNames1;
        for (String s : set) {
            tranNames1 = extendName(s);
            if (setCrossing(tranNames, tranNames1)) {
                t = s;
                break;
            }
        }
        return t;
    }

    private static boolean setCrossing(Set<String> set1, Set<String> set2) {
        boolean bool = false;
        for (String s : set2) {
            if (set1.contains(s)) {
                bool = true;
                break;
            }
            if (!TextUtils.hasChineseChar(s)) {
                for (String string : set1) {
                    if (TextUtils.stringContains(s, string, true)) {
                        bool = true;
                        break;
                    }
                }
            }
        }
        return bool;
    }

    private static Set<String> extendName(String name) {
        Set<String> tranNames = new HashSet<>();
        tranNames.add(TextUtils.getLetters(name));
        tranNames.addAll(special(name));
        return tranNames;
    }

    private static Pattern spaecial_pattern = Pattern.compile("\\((\\w+)\\)");

    private static List<String> special(String name) {
        List<String> all = new ArrayList<String>();
        String tn = TextUtils.getLetters(name.replaceAll("\\[.*?\\]|\\(.*?\\)|【.*？】|（.*？）", ""));
        all.add(tn);
        if (TextUtils.hasChineseChar(name)) {
            all.add(HanLP.convertToPinyinString(tn, "", false));
            List<Pinyin> pinyins = HanLP.convertToPinyinList(name);
            int size = pinyins.size();
            if (size > 1) {
                List<String> list = new ArrayList<String>();
                for (Pinyin pinyin : pinyins) {
                    list.add(pinyin.getPinyinWithoutTone());
                }
                all.add(abbreviation(list, 1));
                all.add(abbreviationReverse(list, 1));
                if (size > 2) {
                    all.add(abbreviation(list, 2));
                    all.add(abbreviationReverse(list, 1));
                }
            }
        } else {
            Matcher m = spaecial_pattern.matcher(name);
            String str;
            while (m.find()) {
                str = name.substring(0, m.start()) + name.substring(m.end(), name.length());
                all.add(TextUtils.getLetters(str));
            }
            all.add(TextUtils.getLetters(name));
            StringBuffer sb1 = new StringBuffer(), sb2 = new StringBuffer();
            String[] items = name.split(",");
            if (items.length > 1) {
                sb1.setLength(0);
                sb2.setLength(0);
                for (int i = 0; i < items.length; i++) {
                    if (i > 0) {
                        sb1.append(items[i]);
                    }
                    if (i < items.length - 1) {
                        sb2.append(items[i]);
                    }
                }
                all.add(TextUtils.getLetters(items[0] + sb1.toString()));
                all.add(TextUtils.getLetters(items[items.length - 1] + sb2.toString()));
            }
        }
        return all;
    }

    private static String abbreviation(List<String> list, int split) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < split; i++) {
            sb.append(list.get(i));
        }

        for (int i = split; i < list.size(); i++) {
            sb.append(list.get(i).substring(0, 1));
        }

        return sb.toString();
    }

    private static String abbreviationReverse(List<String> list, int split) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < split; i++) {
            sb.append(list.get(i).substring(0, 1));
        }

        for (int i = split; i < list.size(); i++) {
            sb.append(list.get(i));
        }

        return sb.toString();
    }

    private static String fineMostLikeAuthour(String[] authorArr, String author) {
        String name = null;
        Set<String> names = new HashSet<>();
        String firstName = HanLP.convertToPinyinString(author.substring(0, 1), "", false);
        for (String au : authorArr) {
            if (au.startsWith(firstName) || au.endsWith(firstName)) {
                names.add(au);
            }
        }
        if (names.size() > 0) {
            if (names.size() == 1) {
                name = names.iterator().next();
            } else {
                Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
                List<String> lastNames = new ArrayList<String>();
                for (int i = 1; i < author.length(); i++) {
                    lastNames.add(HanLP.convertToPinyinString(author.substring(i, i + 1), "", false));
                }

                int num;
                List<String> list;
                for (String n : names) {
                    num = 0;
                    for (String str : lastNames) {
                        if (n.contains(str)) {
                            num++;
                        }
                    }

                    if (num == 0) {
                        continue;
                    }

                    if (map.containsKey(num)) {
                        list = map.get(num);
                    } else {
                        list = new ArrayList<>();
                        map.put(num, list);
                    }
                    list.add(n);
                }

                if (map.size() > 0) {
                    List<Entry<Integer, List<String>>> entries = new ArrayList<>(map.entrySet());
                    Collections.sort(entries, new Comparator<Entry<Integer, List<String>>>() {
                        @Override
                        public int compare(Entry<Integer, List<String>> o1, Entry<Integer, List<String>> o2) {
                            return o2.getKey().compareTo(o1.getKey());
                        }
                    });
                    name = getName(entries.get(0).getValue(), firstName, lastNames);
                }
            }
        }
        return name;
    }

    private static String getName(List<String> names, String firstName, List<String> lastNames) {
        String name = null;
        if (names.size() == 1) {
            name = names.get(0);
        }
        return name;
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;// 有一个中文字符就返回
            }
        }
        return false;
    }

    // 判断一个字符是否是中文  
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

}

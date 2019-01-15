package com.wd.cloud.pdfsearchserver.util;

public class StringUtil {
    public static String repalceSymbol(String str){
        return str.replaceAll("\\.","")
                .replaceAll("-","")
                .replaceAll("<","")
                .replaceAll(">","")
                .replaceAll("《","")
                .replaceAll("》","")
                .replaceAll("_","")
                .replaceAll("'","")
                .replaceAll(" ","")
                .replaceAll(";","")
                .replaceAll("   ","")
                .replaceAll(",","")
                .toLowerCase();

    }

    public static void main(String[] args) {
        String ss = "1.2    -3<4>   5《6》7_8'9;0,WES";
        System.out.println(repalceSymbol(ss));
    }
}

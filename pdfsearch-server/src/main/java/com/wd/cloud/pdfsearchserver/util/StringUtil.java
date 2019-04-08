package com.wd.cloud.pdfsearchserver.util;

public class StringUtil {
    public static String repalceSymbol(String str){
        return str.replaceAll("\\.","")
                .replaceAll("-","")
                .replaceAll("<[.[^>]]*>","")
                //.replaceAll("\\<.*?>","")
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
                .replaceAll(":","")
                .replaceAll("^","")
                .replaceAll("#","")
                .replaceAll("@","")
                .replaceAll("\n","")
                .replaceAll("\r","")
                .toLowerCase();
    }
}

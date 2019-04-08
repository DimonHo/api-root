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

    public static void main(String[] args) {
        String ss = "RopGEF2 is involved in ABA-suppression of seed germination and post-germination growth of\n" +
                " <i>Arabidopsis</i>";
        System.out.println(repalceSymbol(ss));
    }
}

package com.wd.cloud.reportanalysis.service;

import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentGenerationI {


    public JSONObject get(String act, String table, int scid, String compare_scids, String time, String source, int signature);

    public JSONObject getEsi(String table,int scid,String compare_scids,String category_type, int signature);

    public String input(MultipartFile resource);


   public byte[] downLoad(String fileName);

    //   public JSONObject download(String fileName);

}

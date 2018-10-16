package com.wd.cloud.reportanalysis.service;

import net.sf.json.JSONObject;
import org.springframework.core.io.FileSystemResource;

public interface DocumentGenerationI {


    public JSONObject get(String act, String table, int scid, String compare_scids, String time, String source, int signature);

    public JSONObject getEsi(String table,int scid,String compare_scids,String category_type, int signature);

    public String input(FileSystemResource resource);

    //   public JSONObject download(String fileName);

}

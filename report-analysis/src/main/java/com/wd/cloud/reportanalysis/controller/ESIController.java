package com.wd.cloud.reportanalysis.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.google.gson.Gson;
import com.wd.cloud.reportanalysis.entity.school.School;
import com.wd.cloud.reportanalysis.service.DocumentGenerationI;
import com.wd.cloud.reportanalysis.service.SchoolServiceI;
import com.wd.cloud.reportanalysis.util.WordUtil;
import net.sf.json.JSONObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Controller
public class ESIController {

    String jigou_shuming = "";
    int scid = 0;
    //被引学校的scid
    String[] compare_Scid = null;
    //对比机构的对比参数scid
    String compare_scid_value1 = "";
    String compare_scid_value2 = "";
    String compare_scid_value3 = "";
    String compare_scid_value4 = "";
    //第一个图表的数据
    JSONObject jsonObject1 = new JSONObject();
    //目标机构的学校名称
    School mubiao_jigou_amount = null;
    int mubiao_jigou_amount_total = 0;
    //对比机构的学校名称
    School duibi_jigou_amount = null;
    int duibi_jigou_amount_total = 0;
    //第二个对比机构的学校名称
    School duibi_jigou2_amount = null;
    int duibi_jigou2_amount_total = 0;
    //第三个对比机构的学校
    School duibi_jigou3_amount = null;
    int duibi_jigou3_amount_total = 0;
    //第四个对你机构的学校
    School duibi_jigou4_amount = null;
    int duibi_jigou4_amount_total = 0;
    //第二个图表的数据
    JSONObject jsonObject2 = new JSONObject();
    //第三个图表的数据
    JSONObject jsonObject3 = new JSONObject();
    int mubiao_jigoutotal_cited = 0;
    int duibi_jigoutotal_cited = 0;
    int duibi_jigou2total_cited = 0;
    int duibi_jigou3total_cited = 0;
    int duibi_jigou4total_cited = 0;
    //第四个图表的数据
    JSONObject jsonObject4 = new JSONObject();
    //目标机构的学校名称
    School mubiao_jigou_jcr = null;
    int mubiao_jigou_jcr_total = 0;
    String settingPath = "";
    StringBuffer sb = null;
    @Autowired
    private DocumentGenerationI documentGenerationI;
    @Autowired
    private SchoolServiceI schoolServiceI;
    private String filePath = settingPath + ""; //文件路径
    private String fileName; //文件名称
    private String fileOnlyName; //文件唯一名称

    @RequestMapping("/esi")
    public ResponseEntity ESIContrast(@RequestParam int scid, @RequestParam String compare_scids, @RequestParam String category_type, @RequestParam int signature, HttpServletRequest request) throws IOException {
        if (signature == 0) {
            jigou_shuming = "全部";
        } else if (signature == 1) {
            jigou_shuming = "第一机构";
        } else if (signature == 2) {
            jigou_shuming = "通讯机构";
        } else if (signature == 3) {
            jigou_shuming = "第一机构或通讯机构 ";
        } else {
            jigou_shuming = "第一机构且通讯机构 ";
        }
        this.scid = scid;

        //第一个图表的数据
        jsonObject1 = documentGenerationI.getEsi("amount", scid, compare_scids + "", category_type + "", signature);
        jsonObject2 = documentGenerationI.getEsi("jcr", scid, compare_scids + "", category_type + "", signature);
        jsonObject3 = documentGenerationI.getEsi("total_cited", scid, compare_scids + "", category_type + "", signature);

        jsonObject4 = documentGenerationI.getEsi("paper_cited", scid, compare_scids + "", category_type + "", signature);

        String time_update = jsonObject1.getJSONObject("explain").getString("time_update");
        String time_slot = jsonObject1.getJSONObject("explain").getString("time_slot");
        System.out.println("llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll" + jsonObject1);
        //目标机构的学校名称
        mubiao_jigou_amount = schoolServiceI.findByScid(Integer.parseInt(scid + ""));
        mubiao_jigou_amount_total = jsonObject1.getJSONObject("content").getJSONObject(scid + "").getInt("total");

        //第二张表
        mubiao_jigou_jcr = schoolServiceI.findByScid(Integer.parseInt(scid + ""));
        mubiao_jigou_jcr_total = jsonObject2.getJSONObject("content").getJSONObject(scid + "").getInt("total");
        //第三张表总被引频次
        mubiao_jigoutotal_cited = jsonObject3.getJSONObject("content").getJSONObject(scid + "").getInt("cites");

        //解析进来的对比机构的scid
        compare_Scid = compare_scids.split(",");
        List<String> cls = new ArrayList<>();

        for (int i = 0; i < compare_Scid.length; i++) {
            cls.add(compare_Scid[i]);
        }
        compare_scid_value1 = cls.get(0);

        //对比机构的学校名称
        duibi_jigou_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
        duibi_jigou_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1).getInt("total");
        duibi_jigoutotal_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getInt("cites");
        //第二个对比机构的学校名称
        if (compare_Scid.length > 1 && compare_Scid.length < 3) {
            compare_scid_value2 = cls.get(1);
            duibi_jigou2_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            duibi_jigou2_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getInt("total");
            duibi_jigou2total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getInt("cites");
        }
        //第三个对比机构的学校
        if (compare_Scid.length > 2 && compare_Scid.length < 4) {
            compare_scid_value2 = cls.get(1);
            duibi_jigou2_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            duibi_jigou2_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getInt("total");
            duibi_jigou2total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getInt("cites");

            //分割线
            compare_scid_value3 = cls.get(2);
            duibi_jigou3_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3));
            duibi_jigou3_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3).getInt("total");
            duibi_jigou3total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getInt("cites");

        }
        //第四个对比机构的学校
        if (compare_Scid.length > 3 && compare_Scid.length < 5) {
            compare_scid_value2 = cls.get(1);

            duibi_jigou2_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            duibi_jigou2_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getInt("total");
            duibi_jigou2total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getInt("cites");

            //分割线
            compare_scid_value3 = cls.get(2);
            duibi_jigou3_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3));
            duibi_jigou3_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3).getInt("total");
            duibi_jigou3total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getInt("cites");

            //分割线
            compare_scid_value4 = cls.get(3);
            duibi_jigou4_amount = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value4));
            duibi_jigou4_amount_total = jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4).getInt("total");
            duibi_jigou4total_cited = jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getInt("cites");

        }
        CreateTu();
        CreateTu1();
        CreateTu2();
        CreateTu3();


        Map<String, Object> dataMap = new HashMap<String, Object>();


        List<Map<String, Object>> listInfo = new ArrayList<Map<String, Object>>();

        for (int i = 2008; i <= 2018; i++) {
            Map<String, Object> map = new HashMap<>();
            //目标机构的数据
            map.put("mubiao_jigou_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + ""));
            //第三张表
            map.put("mubiao_jigou_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + ""));
            map.put("mubiao_shuju_paper", jsonObject4.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getDouble(i + ""));
            //对比机构的数据
            map.put("duibi_jigou_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1).getJSONObject("list").getInt(i + ""));
            map.put("duibi_jigou_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getInt(i + ""));
            map.put("duibi_shuju_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getDouble(i + ""));

            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                map.put("duibi_jigou2_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou2_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju2_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""));

            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                map.put("duibi_jigou2_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou2_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju2_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""));


                map.put("duibi_jigou3_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou3_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju3_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getDouble(i + ""));

            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                map.put("duibi_jigou2_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou2_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju2_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""));

                map.put("duibi_jigou3_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou3_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju3_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getDouble(i + ""));

                map.put("duibi_jigou4_amount_shuju", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4).getJSONObject("list").getInt(i + ""));
                map.put("duibi_jigou4_total_shuju", jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getInt(i + ""));
                map.put("duibi_shuju4_paper", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getDouble(i + ""));

            }

            map.put("year", i);

            listInfo.add(map);

        }
        //第二个对比机构的数据
        List<Map<String, Object>> listInfo1 = new ArrayList<Map<String, Object>>();
        for (int j = 1; j <= 4; j++) {
            Map<String, Object> map = new HashMap<>();
            map.put("mubiao_jigou_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(j + ""));
            map.put("duibi_jigou_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getInt(j + ""));
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                map.put("duibi_jigou2_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(j + ""));
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                map.put("duibi_jigou2_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(j + ""));
                map.put("duibi_jigou3_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(j + ""));
            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                map.put("duibi_jigou2_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(j + ""));
                map.put("duibi_jigou3_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(j + ""));
                map.put("duibi_jigou4_jcr_shuju", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getInt(j + ""));

            }
            listInfo1.add(map);
        }
        dataMap.put("listInfo", listInfo);
        dataMap.put("listInfo1", listInfo1);
        //第三张表的总被引频次数据
        dataMap.put("mubiao_jigoutotal_cited", mubiao_jigoutotal_cited);
        dataMap.put("duibi_jigoutotal_cited", duibi_jigoutotal_cited);
        dataMap.put("duibi_jigou2total_cited", duibi_jigou2total_cited);
        dataMap.put("duibi_jigou3total_cited", duibi_jigou3total_cited);
        dataMap.put("duibi_jigou4total_cited", duibi_jigou4total_cited);
        //表头的数据
        dataMap.put("category_type", category_type);
        dataMap.put("jigou_shuming", jigou_shuming);
        dataMap.put("time_slot", time_slot);
        dataMap.put("time_update", time_update);
        //图片
        String img1 = getImgStr(filePath + "/7" + sb + ".jpg");
        dataMap.put("shujutu_amount", img1);
        String img2 = getImgStr(filePath + "/8" + sb + ".jpg");
        dataMap.put("shujutu_jcr", img2);
        String img3 = getImgStr(filePath + "/9" + sb + ".jpg");
        dataMap.put("shujutu_total", img3);

        String img4 = getImgStr(filePath + "/10" + sb + ".jpg");
        dataMap.put("shujutu_paper", img4);
        //目标机构的数据
        dataMap.put("mubiao_jigou_amount", mubiao_jigou_amount.getName());
        dataMap.put("mubiao_jigou_amount_total", mubiao_jigou_amount_total);
        dataMap.put("mubiao_jigou_paper_total", jsonObject4.getJSONObject("content").getJSONObject(scid + "").getInt("paper_cites"));
        //第一个对比机构的数据
        dataMap.put("duibi_jigou_amount", duibi_jigou_amount.getName());
        dataMap.put("duibi_jigou_amount_total", duibi_jigou_amount_total);
        dataMap.put("duibi_jigou_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getDouble("paper_cites"));


        //第二个对比机构的数据
        if (compare_Scid.length > 1 && compare_Scid.length < 3) {
            dataMap.put("duibi_jigou2_amount", duibi_jigou2_amount.getName());
            dataMap.put("duibi_jigou2_amount_total", duibi_jigou2_amount_total);
            dataMap.put("duibi_jigou2_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getDouble("paper_cites"));

        }
        if (compare_Scid.length > 2 && compare_Scid.length < 4) {
            dataMap.put("duibi_jigou2_amount", duibi_jigou2_amount.getName());
            dataMap.put("duibi_jigou2_amount_total", duibi_jigou2_amount_total);
            dataMap.put("duibi_jigou2_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getDouble("paper_cites"));

            //第三个对比机构的数据
            dataMap.put("duibi_jigou3_amount", duibi_jigou3_amount.getName());
            dataMap.put("duibi_jigou3_amount_total", duibi_jigou3_amount_total);
            dataMap.put("duibi_jigou3_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getDouble("paper_cites"));

        }
        if (compare_Scid.length > 3 && compare_Scid.length < 5) {
            dataMap.put("duibi_jigou2_amount", duibi_jigou2_amount.getName());
            dataMap.put("duibi_jigou2_amount_total", duibi_jigou2_amount_total);
            dataMap.put("duibi_jigou2_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getDouble("paper_cites"));


            //第三个对比机构的数据
            dataMap.put("duibi_jigou3_amount", duibi_jigou3_amount.getName());
            dataMap.put("duibi_jigou3_amount_total", duibi_jigou3_amount_total);
            dataMap.put("duibi_jigou3_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getDouble("paper_cites"));


            //第四个对比机构的数据
            dataMap.put("duibi_jigou4_amount", duibi_jigou4_amount.getName());
            dataMap.put("duibi_jigou4_amount_total", duibi_jigou4_amount_total);
            dataMap.put("duibi_jigou4_paper_total", jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getDouble("paper_cites"));

        }
        //得到总发文量最高的机构是：；中南大学发文量最多的年份是：年
        String max_school_amount = "";
        int max_total_amount = 0;
        for (int i = 0; i < compare_Scid.length; i++) {
            int totals = jsonObject1.getJSONObject("content").getJSONObject(cls.get(i)).getInt("total");
            if (max_total_amount < totals) {
                max_total_amount = totals;
                max_school_amount = cls.get(i);
            }
        }
        School max_School_amount = schoolServiceI.findByScid(Integer.parseInt(max_school_amount));
        dataMap.put("max_School_amount", max_School_amount.getName());
        //获得中南大学发文量最多的机构
        int max_total_amount_scid = 0;
        int max_year_amount = 0;
        for (int i = 2008; i <= 2018; i++) {
            int totals = jsonObject1.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + "");
            if (max_total_amount_scid < totals) {
                max_total_amount_scid = totals;
                max_year_amount = i;
            }
        }
        dataMap.put("max_year_amount", max_year_amount);

        int compare_maxtotal = jsonObject2.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt("1");
        String compare_scidmax = scid + "";
        //得到Q1区域论文数量最多和第二多的机构
        for (int i = 0; i < compare_Scid.length; i++) {
            int total = jsonObject2.getJSONObject("content").getJSONObject(cls.get(i)).getJSONObject("list").getInt("1");
            if (compare_maxtotal < total) {
                compare_maxtotal = total;
                compare_scidmax = cls.get(i);
            }
        }
        School max_school = schoolServiceI.findByScid(Integer.parseInt(compare_scidmax));
        dataMap.put("max_school", max_school.getName());

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> maps = jsonObject2.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list");
        maps.put("ww", scid);
        for (int i = 0; i < compare_Scid.length; i++) {
            Map<String, Object> map = jsonObject2.getJSONObject("content").getJSONObject(cls.get(i)).getJSONObject("list");
            map.put("ww", cls.get(i));
            list.add(map);
        }
        list.add(maps);
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                double val1 = Double.parseDouble(o1.get("1").toString());
                double val2 = Double.parseDouble(o2.get("1").toString());
                if (val1 > val2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        String compare_scid_er = list.get(1).get("ww").toString();
        School sch_er = schoolServiceI.findByScid(Integer.parseInt(compare_scid_er));
        dataMap.put("sch_er", sch_er.getName());
        //对比机构中，总被引频次最高的机构是：；中南大学被引频次最高的年份是年
        int max_jigou = 0;
        String max_scid = "";
        for (int i = 0; i < compare_Scid.length; i++) {
            int totals = jsonObject3.getJSONObject("content").getJSONObject(cls.get(i)).getInt("cites");
            if (max_jigou < totals) {
                max_jigou = totals;
                max_scid = cls.get(i);

            }
        }
        School school_mx = schoolServiceI.findByScid(Integer.parseInt(max_scid));
        dataMap.put("school_mx", school_mx.getName());
        int max_tal = 0;
        int max_year = 0;
        for (int i = 2008; i <= 2018; i++) {
            int totals = jsonObject3.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + "");
            if (max_tal < totals) {
                max_tal = totals;
                max_year = i;
            }
        }

        //.........................对比机构中，均被引频次最高的机构是：；中南大学篇均被引频次最高的年份是年
        double max_total_paper = 0.00;
        String compare_scid_paper = "";
        for (int i = 0; i < compare_Scid.length; i++) {
            Double totals = jsonObject4.getJSONObject("content").getJSONObject(cls.get(i)).getDouble("paper_cites");
            if (max_total_paper < totals) {
                max_total_paper = totals;
                compare_scid_paper = cls.get(i);
            }
        }
        School max_sch = schoolServiceI.findByScid(Integer.parseInt(compare_scid_paper));
        dataMap.put("max_sch", max_sch.getName());


        int max_year_total = 0;
        int ma_year = 0;
        for (int i = 2008; i < 2018; i++) {
            int years_totals = jsonObject4.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + "");
            if (max_year_total < years_totals) {
                max_year_total = years_totals;
                ma_year = i;
            }
        }
        dataMap.put("ma_year", ma_year);

        dataMap.put("max_year", max_year);
        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));

        Setting setting = new Setting("word/", true);
        String settingPath = setting.getSettingPath();
        this.settingPath = settingPath;

        //文件路径
        filePath = settingPath + "";

        //文件唯一名称
        fileOnlyName = "用freemarker生成Word文档_" + sb + ".doc";

        //文件名称
        fileName = "用freemarker生成Word文档.doc";

        /** 生成word */
        WordUtil.createWord(dataMap, "ESI对比分析.ftl", filePath, fileOnlyName);

        File tFile = new File(filePath + "" + fileOnlyName);
        FileInputStream input = new FileInputStream(tFile);
        MultipartFile multipartFile = new MockMultipartFile("file", tFile.getName(), "text/plain", IOUtils.toByteArray(input));
        String fileName = documentGenerationI.input(multipartFile);
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(fileName, map.getClass());
        String filename = (String) map.get("fileId");
        byte[] bytes = documentGenerationI.downLoad(filename);
        HttpHeaders headers = new HttpHeaders();
        String disposition = StrUtil.format("attachment; filename=\"{}\"", filename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", disposition);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().contentLength(bytes.length)
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .headers(headers)
                .body(bytes);

//        FileObjModel fileObjModel = fileService.getFileToHbase("journalImage", filename);
////        return ResponseEntity
////                .ok()
////                .headers(HttpHeaderUtil.buildHttpHeaders(filename,request ))
////                .contentLength(fileObjModel.getFileByte().length)
////                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
////                .body(fileObjModel.getFileByte());

    }

    private void CreateTu() {
        CategoryDataset ds = getDataSet();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "发文量对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis = plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath + "/7" + sb + ".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //第二张表的数据图
    private void CreateTu1() {
        CategoryDataset ds = getDataSet1();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "JCR分区对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis = plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath + "/8" + sb + ".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //第三张图
    private void CreateTu2() {
        CategoryDataset ds = getDataSet2();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "被引频次对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis = plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath + "/9" + sb + ".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //第三张图
    private void CreateTu3() {
        CategoryDataset ds = getDataSet2();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "篇均被引频次对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis = plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath + "/10" + sb + ".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CategoryDataset getDataSet() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 2008; i <= 2018; i++) {
            ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + ""), mubiao_jigou_amount.getName() + "", i + "年");
            ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getInt(i + ""), duibi_jigou_amount.getName() + "", i + "年");
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getInt(i + ""), duibi_jigou4_amount.getName() + "", i + "年");

            }
        }

        return ds;
    }

    private CategoryDataset getDataSet1() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 0; i < 4; i++) {
            ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt((i + 1) + ""), mubiao_jigou_amount.getName() + "", "Q" + (i + 1) + "区");
            ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou_amount.getName() + "", "Q" + (i + 1) + "区");
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou2_amount.getName() + "", "Q" + (i + 1) + "区");
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou2_amount.getName() + "", "Q" + (i + 1) + "区");
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou3_amount.getName() + "", "Q" + (i + 1) + "区");
            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou2_amount.getName() + "", "Q" + (i + 1) + "区");
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou3_amount.getName() + "", "Q" + (i + 1) + "区");
                ds.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getInt((i + 1) + ""), duibi_jigou4_amount.getName() + "", "Q" + (i + 1) + "区");

            }
        }

        return ds;
    }

    private CategoryDataset getDataSet2() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 2008; i <= 2018; i++) {
            ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getInt(i + ""), mubiao_jigou_amount.getName() + "", i + "年");
            ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getInt(i + ""), duibi_jigou_amount.getName() + "", i + "年");
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getInt(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getInt(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
                ds.addValue(jsonObject3.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getInt(i + ""), duibi_jigou4_amount.getName() + "", i + "年");

            }
        }

        return ds;
    }

    private CategoryDataset getDataSet3() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 2008; i <= 2018; i++) {
            ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(scid + "").getJSONObject("list").getDouble(i + ""), mubiao_jigou_amount.getName() + "", i + "年");
            ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value1 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou_amount.getName() + "", i + "年");
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
            }
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value2 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou2_amount.getName() + "", i + "年");
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value3 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou3_amount.getName() + "", i + "年");
                ds.addValue(jsonObject4.getJSONObject("content").getJSONObject(compare_scid_value4 + "").getJSONObject("list").getDouble(i + ""), duibi_jigou4_amount.getName() + "", i + "年");

            }
        }

        return ds;
    }

    public String getImgStr(String imgFilePath) throws IOException {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        in = new FileInputStream(imgFilePath);
        data = new byte[in.available()];
        in.read(data);
        in.close();
        return new String(Base64.encodeBase64(data));
    }

    /**
     * 下载生成的word文档
     */
    public String dowloadWord() {
        /** 先判断文件是否已生成  */
        try {
            //解决中文乱码
            filePath = URLDecoder.decode(filePath, "UTF-8");
            fileOnlyName = URLDecoder.decode(fileOnlyName, "UTF-8");
            fileName = URLDecoder.decode(fileName, "UTF-8");

            //如果文件不存在，则会跳入异常，然后可以进行异常处理
            new FileInputStream(filePath + File.separator + fileOnlyName);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "dowloadWord";
    }

    /**
     * 返回最终生成的word文档 文件流
     * 下载生成的word文档
     */
    public InputStream getWordFile() {
        try {
            //解决中文乱码
            fileName = URLDecoder.decode(fileName, "UTF-8");

            /** 返回最终生成的word文件流  */
            return new FileInputStream(filePath + File.separator + fileOnlyName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFileOnlyName() {
        return fileOnlyName;
    }


    public void setFileOnlyName(String fileOnlyName) {
        this.fileOnlyName = fileOnlyName;
    }

}

package com.wd.cloud.reportanalysis.controller;
import cn.hutool.setting.Setting;
import com.wd.cloud.reportanalysis.entity.school.School;
import com.wd.cloud.reportanalysis.service.DocumentGenerationI;
import com.wd.cloud.reportanalysis.service.SchoolServiceI;
import com.wd.cloud.reportanalysis.util.WordUtil;
import net.sf.json.JSONObject;
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
public class ZoningContrast {

    @Autowired
    private DocumentGenerationI documentGenerationI;

    @Autowired
    private SchoolServiceI schoolServiceI;


    String jigou_shuming="";
    School mubiao_jigou=null;
    String[] compare_Scid=null;
    //第一个对比机构数据
    School duibi_jigou1=null;
    int total1=0;
    JSONObject duibi1_content;

    //第二个对比机构数据
    School duibi_jigou2=null;
    int total2=0;
    JSONObject content2=null;
    JSONObject duibi2_content;
    JSONObject duibi2_content_zky_1;

    //第三个对比机构数据
    School duibi_jigou3=null;
    int total3=0;
    JSONObject content3=null;
    JSONObject duibi3_content;
    JSONObject duibi3_content_zky_1;

    //第四个对比机构的数据
    School duibi_jigou4=null;
    int total4=0;
    JSONObject content4=null;
    JSONObject duibi4_content;
    JSONObject duibi4_content_zky_1;

    //目标机构的数据
    int mubiao_total=0;
    JSONObject mubiao_content;
    JSONObject mubiao_content_zky_1;
    //论文数量最多机构
    School lunwen_max_school=null;
    //得到定义不同的表名
    String tables="jcr,jcr_zky_1,jcr_zky_2";
    String[] table_fenqu=null;
    //............
    List<Map<String, Object>> listInfo=null;
    //第二个图表
    JSONObject jsonObject1=null;
    //第三个图表
    JSONObject jsonObject2=null;
    int scid=0;

    String compare_scid_value="";
    String compare_scid_value2="";
    String compare_scid_value3="";
    String compare_scid_value4="";

    String settingPath="";

    private String filePath=settingPath+""; //文件路径
    private String fileName; //文件名称
    private String fileOnlyName; //文件唯一名称
    StringBuffer sb=null;
    @RequestMapping("/fenqu")
    public byte[] createZoning(@RequestParam String act, @RequestParam String table, @RequestParam int scid, @RequestParam String compare_scids, @RequestParam String time, @RequestParam String source, @RequestParam int signature) throws IOException{

        JSONObject jsonObject=new JSONObject();
        jsonObject1=new JSONObject();
        this.scid=scid;
        //根据三个不同的参数得到不同的数据
        table_fenqu=tables.split(",");
        String table_s="";
        List<String> table_list=new ArrayList<>();
        for (int i=0;i<3;i++){
            table_list.add(table_fenqu[i]);

        }
        table_s= table_list.get(0);
        jsonObject= documentGenerationI.get(act+"","jcr",scid,compare_scids+"",time+"",source+"",signature);
        jsonObject1=documentGenerationI.get(act+"","jcr_zky_1",scid,compare_scids+"",time+"",source+"",signature);
        jsonObject2=documentGenerationI.get(act+"","jcr_zky_2",scid,compare_scids+"",time+"",source+"",signature);
        //第一个表
        //jsonObject= documentGenerationI.get(act+"",table_s+"",scid,compare_scids+"",time+"",source+"",signature);
        //获得机构署名
        if(signature==0){
            jigou_shuming="全部";
        }else if(signature==1){
            jigou_shuming="第一机构";
        }else if(signature==2){
            jigou_shuming="通讯机构";
        }else if(signature==3){
            jigou_shuming="第一机构或通讯机构 ";
        }else {
            jigou_shuming="第一机构且通讯机构 ";
        }
        JSONObject names=jsonObject.getJSONObject("content");
        JSONObject names1=jsonObject1.getJSONObject("content");
        JSONObject names2=jsonObject2.getJSONObject("content");
        //目标机构数据
        JSONObject content=names.getJSONObject(scid+"");
        JSONObject content1=names1.getJSONObject(scid+"");
        //目标机构的学校名称
        mubiao_jigou=schoolServiceI.findByScid(Integer.parseInt(scid+""));
        mubiao_total=content.getInt("total");
        mubiao_content=content.getJSONObject("list");
        mubiao_content_zky_1=content1.getJSONObject("list");
        //获得对比机构的学校和信息
        compare_Scid=compare_scids.split(",");
        List<String> cls=new ArrayList<>();

        if(compare_Scid.length>0&&compare_Scid.length<2){
            for(int i=0;i<compare_Scid.length;i++){
                cls.add(compare_Scid[i]);
            }
            compare_scid_value = cls.get(0);
            content1 = names.getJSONObject(compare_scid_value);

            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value + ""));
            total1 = content1.getInt("total");
            duibi1_content = content1.getJSONObject("list");
        }else if(compare_Scid.length>1&&compare_Scid.length<3){
            for(int i=0;i<compare_Scid.length;i++){
                cls.add(compare_Scid[i]);

            }
            compare_scid_value = cls.get(0);
            compare_scid_value2=cls.get(1);
            //第一个对比机构数据
            content1 = names.getJSONObject(compare_scid_value);
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value + ""));
            total1 = content1.getInt("total");
            duibi1_content = content1.getJSONObject("list");
            //第二个对比机构数据
            content2 = names.getJSONObject(compare_scid_value2);
            duibi_jigou2 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2 + ""));
            total2 = content2.getInt("total");
            duibi2_content = content2.getJSONObject("list");
        }else if(compare_Scid.length>2&&compare_Scid.length<4){
            for(int i=0;i<compare_Scid.length;i++){
                cls.add(compare_Scid[i]);

            }
            compare_scid_value = cls.get(0);
            compare_scid_value2=cls.get(1);
            compare_scid_value3=cls.get(2);
            //第一个对比机构数据
            content1 = names.getJSONObject(compare_scid_value);
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value + ""));
            total1 = content1.getInt("total");
            duibi1_content = content1.getJSONObject("list");
            //第二个对比机构数据
            content2 = names.getJSONObject(compare_scid_value2);
            duibi_jigou2 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2 + ""));
            total2 = content2.getInt("total");
            duibi2_content = content2.getJSONObject("list");
            //第三个对比机构
            content3 = names.getJSONObject(compare_scid_value3);
            duibi_jigou3 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3 + ""));
            total3 = content3.getInt("total");
            duibi3_content = content3.getJSONObject("list");
        }else if(compare_Scid.length>3&&compare_Scid.length<5){
            for(int i=0;i<compare_Scid.length;i++){
                cls.add(compare_Scid[i]);

            }
            compare_scid_value = cls.get(0);
            compare_scid_value2=cls.get(1);
            compare_scid_value3=cls.get(2);
            compare_scid_value4=cls.get(3);
            //第一个对比机构数据
            content1 = names.getJSONObject(compare_scid_value);
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value + ""));
            total1 = content1.getInt("total");
            duibi1_content = content1.getJSONObject("list");
            //第二个对比机构数据
            content2 = names.getJSONObject(compare_scid_value2);
            duibi_jigou2 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2 + ""));
            total2 = content2.getInt("total");
            duibi2_content = content2.getJSONObject("list");
            //第三个对比机构
            content3 = names.getJSONObject(compare_scid_value3);
            duibi_jigou3 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3 + ""));
            total3 = content3.getInt("total");
            duibi3_content = content3.getJSONObject("list");
            //第四个
            content4 = names.getJSONObject(compare_scid_value4);
            duibi_jigou4 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value4 + ""));
            total4 = content4.getInt("total");
            duibi4_content = content4.getJSONObject("list");
        }
            //分割线
            int compare_maxtotal = names.getJSONObject(scid+"").getJSONObject("list").getInt("1");
            String compare_scidmax = scid+"";
            //得到Q1区域论文数量最多和第二多的机构
        for(int i=0;i<compare_Scid.length;i++) {
            int total = names.getJSONObject(cls.get(i)).getJSONObject("list").getInt("1");
            if (compare_maxtotal < total) {
                compare_maxtotal = total;
                compare_scidmax = cls.get(i);
            }

        }


            listInfo = new ArrayList<Map<String, Object>>();

            for (int j = 1; j <= 4; j++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mubiao_shuju", mubiao_content.getInt(j + ""));
                    map.put("duibi1_shuju", duibi1_content.getInt(j + ""));
                    //第一个表的数据
                    map.put("mubiao_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(j + ""));
                    map.put("mubiao_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi1_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi1_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value+"").getJSONObject("list").getInt(j + ""));
                if(compare_Scid.length>1&&compare_Scid.length<3){

                    map.put("duibi2_shuju",duibi2_content.getInt(j + ""));

                    //第二个表的数据
                    map.put("duibi2_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi2_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));
                    //第三个表的数据
                }
                if(compare_Scid.length>2&&compare_Scid.length<4){
                    map.put("duibi2_shuju",duibi2_content.getInt(j + ""));
                    map.put("duibi2_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi2_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));

                    map.put("duibi3_shuju",duibi3_content.getInt(j + ""));
                    map.put("duibi3_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi3_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(j + ""));
                }
                if(compare_Scid.length>3&&compare_Scid.length<5){
                    map.put("duibi2_shuju",duibi2_content.getInt(j + ""));
                    map.put("duibi2_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi2_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(j + ""));

                    map.put("duibi3_shuju",duibi3_content.getInt(j + ""));
                    map.put("duibi3_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi3_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(j + ""));

                    map.put("duibi4_shuju",duibi4_content.getInt(j + ""));
                    map.put("duibi4_shuju_zky_1", jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt(j + ""));
                    map.put("duibi4_shuju_zky_2", jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt(j + ""));
                }
                listInfo.add(map);
            }

            //得到Q1区域论文数量最多的机构
           lunwen_max_school = schoolServiceI.findByScid(Integer.parseInt(compare_scidmax));
            // CategoryDataset ds = getDataSet();
            CategoryDataset ds = getDataSet();

            JFreeChart chart = ChartFactory.createBarChart3D(
                    "分区对比分析", //图表标题
                    "分区", //目录轴的显示标签
                    "论文数量", //数值轴的显示标签
                    ds, //数据集
                    PlotOrientation.VERTICAL, //图表方向
                    true, //是否显示图例，对于简单的柱状图必须为false
                    false, //是否生成提示工具
                    false
            );
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
                out = new FileOutputStream(filePath + "/2"+sb+".jpg");
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

            //第二张图表
        CategoryDataset ds1 = getDataSet1();

        JFreeChart chart1 = ChartFactory.createBarChart3D(
                "中科院JCR分区(大类)对比分析", //图表标题
                "分区", //目录轴的显示标签
                "论文数量", //数值轴的显示标签
                ds1, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false
        );
        CategoryPlot plot1 = chart1.getCategoryPlot();
        CategoryAxis domainAxis1 = plot1.getDomainAxis();
        domainAxis1.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis1.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis1 = plot1.getRangeAxis();
        rangeAxis1.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis1.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart1.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart1.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out1 = null;
        try {
            out1 = new FileOutputStream(filePath + "/3"+sb+".jpg");
            ChartUtilities.writeChartAsJPEG(out1, 0.5f, chart1, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //第三张图表
        CategoryDataset ds2 = getDataSet1();

        JFreeChart chart2 = ChartFactory.createBarChart3D(
                "中科院JCR分区(小类)对比分析", //图表标题
                "分区", //目录轴的显示标签
                "论文数量", //数值轴的显示标签
                ds2, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false
        );
        CategoryPlot plot2 = chart2.getCategoryPlot();
        CategoryAxis domainAxis2 = plot2.getDomainAxis();
        domainAxis2.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis2.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis2 = plot2.getRangeAxis();
        rangeAxis2.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis2.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart2.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart2.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out2 = null;
        try {
            out2 = new FileOutputStream(filePath + "/4"+sb+".jpg");
            ChartUtilities.writeChartAsJPEG(out2, 0.5f, chart2, 800, 400, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


            Map<String, Object> dataMap = new HashMap<String, Object>();
            /** 组装数据 */
            String img1 = getImgStr(filePath + "/2"+sb+".jpg");
            String img2 = getImgStr(filePath + "/3"+sb+".jpg");
            String img3 = getImgStr(filePath + "/4"+sb+".jpg");
            //得到最高和第二高的机构民称
            //jcr分区
            Map<String, Object> map5 = GetMaxAndTwoSchool(scid, names, cls);
            School school_jcr= (School) map5.get("sch_er");
            School school1_jcr= (School) map5.get("sch_one");
            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"+school_jcr.getName()+"kkkdcddddddd"+school1_jcr.getName());
            dataMap.put("school1_jcr",school1_jcr.getName());

            Map<String, Object> map6 = GetMaxAndTwoSchool(scid, names1, cls);
            School school_jcr_zky_1=(School) map5.get("sch_er");
            School school1_jcr_zky_1= (School) map5.get("sch_one");
            dataMap.put("school_jcr_zky_1",school_jcr_zky_1.getName());
            dataMap.put("school1_jcr_zky_1",school1_jcr_zky_1.getName());

            Map<String, Object> map7 = GetMaxAndTwoSchool(scid, names2, cls);
            School school_jcr_zky_2=(School) map5.get("sch_er");
            School school1_jcr_zky_2= (School) map5.get("sch_one");
            dataMap.put("school_jcr_zky_2",school_jcr_zky_2.getName());
            dataMap.put("school1_jcr_zky_2",school1_jcr_zky_2.getName());
        //分割线
            dataMap.put("shuju_tu", img1);
            dataMap.put("shuju_tu_zky_1",img2);
            dataMap.put("shuju_tu_zky_2",img3);
            //存放论文最多数量的机构
            dataMap.put("lunwen_max_school", lunwen_max_school.getName());
            //存放表头的数据
            dataMap.put("source", source);
            dataMap.put("jigou_shuming", jigou_shuming);
            //存放一组数据
            //目标机构
            dataMap.put("listInfo", listInfo);

            dataMap.put("mubiao_total", mubiao_total);
            dataMap.put("mubiao_jigou", mubiao_jigou.getName());
            List<Map<String, Object>> listInfo1=null;
            Map<String, Object> map1=null;
        if(compare_Scid.length>0&&compare_Scid.length<2) {
                //第一个对比机构的数据
                 listInfo1 = new ArrayList<Map<String, Object>>();
                map1 = new HashMap<>();
                map1.put("duibi_jigou1", duibi_jigou1.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map1.put("total1", total1);
                //dataMap.put("total1", total1);
                listInfo1.add(map1);
                dataMap.put("listInfo1", listInfo1);
                dataMap.put("content1", content1);
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + content1);
            }
            //第一个对比机构的数据
            //第二个对比机构的数据
            List<Map<String, Object>> listInfo2=null;
            Map<String,Object> map2=null;
            if(compare_Scid.length>1&&compare_Scid.length<3){
                listInfo1 = new ArrayList<Map<String, Object>>();
                map1 = new HashMap<>();
                map1.put("duibi_jigou1", duibi_jigou1.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map1.put("total1", total1);
                //dataMap.put("total1", total1);
                listInfo1.add(map1);
                dataMap.put("listInfo1", listInfo1);
                dataMap.put("content1", content1);
                //
                listInfo2 = new ArrayList<Map<String, Object>>();
                map2=new HashMap<>();
                map2.put("duibi_jigou2",duibi_jigou2.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map2.put("total2", total2);
                listInfo2.add(map2);
                dataMap.put("listInfo2",listInfo2);
                dataMap.put("content2", content2);
            }
            List<Map<String, Object>> listInfo3=null;
            Map<String,Object> map3=null;
            if(compare_Scid.length>2&&compare_Scid.length<4){
                listInfo1 = new ArrayList<Map<String, Object>>();
                map1 = new HashMap<>();
                map1.put("duibi_jigou1", duibi_jigou1.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map1.put("total1", total1);
                //dataMap.put("total1", total1);
                listInfo1.add(map1);
                dataMap.put("listInfo1", listInfo1);
                dataMap.put("content1", content1);
                //
                listInfo2 = new ArrayList<Map<String, Object>>();
                map2=new HashMap<>();
                map2.put("duibi_jigou2",duibi_jigou2.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map2.put("total2", total2);
                listInfo2.add(map2);
                dataMap.put("listInfo2",listInfo2);
                dataMap.put("content2", content2);
                //第三个
                listInfo3 = new ArrayList<Map<String, Object>>();
                map3=new HashMap<>();
                map3.put("duibi_jigou3",duibi_jigou3.getName());
                //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
                map3.put("total3", total3);
                listInfo3.add(map3);
                dataMap.put("listInfo3",listInfo3);
                dataMap.put("content3", content3);
            }
            List<Map<String, Object>> listInfo4=null;
            Map<String,Object> map4=null;
        if(compare_Scid.length>3&&compare_Scid.length<5){
            listInfo1 = new ArrayList<Map<String, Object>>();
            map1 = new HashMap<>();
            map1.put("duibi_jigou1", duibi_jigou1.getName());
            //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            map1.put("total1", total1);
            //dataMap.put("total1", total1);
            listInfo1.add(map1);
            dataMap.put("listInfo1", listInfo1);
            dataMap.put("content1", content1);
            //
            listInfo2 = new ArrayList<Map<String, Object>>();
            map2=new HashMap<>();
            map2.put("duibi_jigou2",duibi_jigou2.getName());
            //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            map2.put("total2", total2);
            listInfo2.add(map2);
            dataMap.put("listInfo2",listInfo2);
            dataMap.put("content2", content2);
            //第三个
            listInfo3 = new ArrayList<Map<String, Object>>();
            map3=new HashMap<>();
            map3.put("duibi_jigou3",duibi_jigou3.getName());
            //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            map3.put("total3", total3);
            listInfo3.add(map3);
            dataMap.put("listInfo3",listInfo3);
            dataMap.put("content3", content3);
            //第四个
            listInfo4 = new ArrayList<Map<String, Object>>();
            map4=new HashMap<>();
            map4.put("duibi_jigou4",duibi_jigou4.getName());
            //dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            map4.put("total4", total4);
            listInfo4.add(map4);
            dataMap.put("listInfo4",listInfo4);
            dataMap.put("content4", content4);
        }
            //生成文档的格式
            Random r = new Random();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            sb = new StringBuffer();
            sb.append(sdf1.format(new Date()));
            sb.append("_");
            sb.append(r.nextInt(100));

            Setting setting = new Setting("word",true);
            String settingPath = setting.getSettingPath();
            this.settingPath=settingPath;
            filePath = settingPath+"";

            //文件唯一名称
            fileOnlyName = "用freemarker生成Word文档_" + sb + ".doc";

            //文件名称
            fileName = "用freemarker生成Word文档.doc";
            WordUtil.createWord(dataMap, "分区对比分析.ftl", filePath, fileOnlyName);

            File tFile=new File(filePath+""+fileOnlyName);
            FileInputStream input = new FileInputStream(tFile);
            MultipartFile multipartFile = new MockMultipartFile("file", tFile.getName(), "text/plain", IOUtils.toByteArray(input));
            String fileName = documentGenerationI.input(multipartFile);
            byte[] bytes = documentGenerationI.downLoad(fileName);
            System.out.println(fileName);

            return bytes;

    }
    School sch_er=null;


    public Map<String,Object> GetMaxAndTwoSchool(@RequestParam int scid, JSONObject names, List<String> cls) {
        List<Map<String,Object>> list = new ArrayList<>();
        List<Map<String,Object>> list1 = new ArrayList<>();
        Map<String,Object> maps = names.getJSONObject(scid+"").getJSONObject("list");
        Map<String,Object> map_jcr=new HashMap<>();
        maps.put("ww",scid);
        for(int i=0;i<compare_Scid.length;i++) {
            Map<String,Object> map  =  names.getJSONObject(cls.get(i)).getJSONObject("list");
            map.put("ww",cls.get(i));
            list.add(map);
        }
       // System.out.println(maps.get("1"));
        list.add(maps);
        Collections.sort(list, new Comparator<Map<String,Object>>() {
            @Override
            public int compare(Map<String,Object> o1, Map<String,Object> o2) {
                double val1 = Double.parseDouble(o1.get("1").toString());
                double val2 = Double.parseDouble(o2.get("1").toString());
                if(val1 > val2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        String compare_scid_er=list.get(1).get("ww").toString();
        String compare_scid_one=list.get(0).get("ww").toString();
        sch_er=schoolServiceI.findByScid(Integer.parseInt(compare_scid_er));
        School sch_one=schoolServiceI.findByScid(Integer.parseInt(compare_scid_one));
        map_jcr.put("sch_er",sch_er);
        map_jcr.put("sch_one",sch_one);

        return map_jcr;
    }




    private  CategoryDataset getDataSet1()
    {
        DefaultCategoryDataset ds1 = new DefaultCategoryDataset();
        for(int i=0;i<4;i++) {
                ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt((i+1) + ""), mubiao_jigou.getName() + "", (i+1) + "区");
                ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou1.getName() + "", (i+1) + "区");
                if(compare_Scid.length>1&&compare_Scid.length<3){
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                }
                if(compare_Scid.length>2&&compare_Scid.length<4){
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
                }
                if(compare_Scid.length>3&&compare_Scid.length<5){
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
                    ds1.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou4.getName() + "", (i+1) + "区");
                }
        }

        return ds1;
    }

    private  CategoryDataset getDataSet2()
    {
        DefaultCategoryDataset ds2 = new DefaultCategoryDataset();
        for(int i=0;i<4;i++) {
            ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt((i+1) + ""), mubiao_jigou.getName() + "", (i+1) + "区");
            ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou1.getName() + "", (i+1) + "区");
            if(compare_Scid.length>1&&compare_Scid.length<3){
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
            }
            if(compare_Scid.length>2&&compare_Scid.length<4){
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
            }
            if(compare_Scid.length>3&&compare_Scid.length<5){
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
                ds2.addValue(jsonObject2.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt((i+1) + ""), duibi_jigou4.getName() + "", (i+1) + "区");
            }
        }

        return ds2;
    }


    private  CategoryDataset getDataSet()
    {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for(int i=0;i<4;i++) {
            ds.addValue(mubiao_content.getInt((i+1) + ""), mubiao_jigou.getName() + "", (i+1) + "区");
            ds.addValue(duibi1_content.getInt((i+1) + ""), duibi_jigou1.getName() + "", (i+1) + "区");
            if(compare_Scid.length>1&&compare_Scid.length<3){
                ds.addValue(duibi2_content.getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
            }
            if(compare_Scid.length>2&&compare_Scid.length<4){
                ds.addValue(duibi2_content.getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                ds.addValue(duibi3_content.getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
            }
            if(compare_Scid.length>3&&compare_Scid.length<5){
                ds.addValue(duibi2_content.getInt((i+1) + ""), duibi_jigou2.getName() + "", (i+1) + "区");
                ds.addValue(duibi3_content.getInt((i+1) + ""), duibi_jigou3.getName() + "", (i+1) + "区");
                ds.addValue(duibi4_content.getInt((i+1) + ""), duibi_jigou4.getName() + "", (i+1) + "区");
            }
        }

        return ds;
    }


    public  String getImgStr(String imgFilePath) throws IOException {
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
}

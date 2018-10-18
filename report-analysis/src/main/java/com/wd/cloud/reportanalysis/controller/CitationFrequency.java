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
public class CitationFrequency {



    @Autowired
    private DocumentGenerationI documentGenerationI;

    @Autowired
    private SchoolServiceI schoolServiceI;

    JSONObject jsonObject=new JSONObject();
    JSONObject jsonObject1=new JSONObject();
    String jigou_shuming="";
    int scid=0;
    int start_year=0;
    int end_year=0;
    //被引学校的scid
    String[] compare_Scid=null;

    String compare_scid_value1="";
    String compare_scid_value2="";
    String compare_scid_value3="";
    String compare_scid_value4="";

    //目标机构的数据
    int mubiao_total=0;
    int mubiao_cited=0;
    School mubiao_jigou=null;
    //对比机构的数据
    int duibi_total=0;
    int duibi_cited=0;
    School duibi_jigou=null;
    //第二个对比机构的数据
    int duibi2_total=0;
    int duibi2_cited=0;
    School duibi2_jigou=null;
    //第三个对比机构的数据
    int duibi3_total=0;
    int duibi3_cited=0;
    School duibi3_jigou=null;
    //第四个对比机构的数据
    int duibi4_total=0;
    int duibi4_cited=0;
    School duibi4_jigou=null;

    String settingPath="";

    private String filePath=settingPath+""; //文件路径
    private String fileName; //文件名称
    private String fileOnlyName; //文件唯一名称
    StringBuffer sb=null;
    @RequestMapping("/frequency")
    public byte[] CitationFrequency (@RequestParam String act, @RequestParam String table, @RequestParam int scid, @RequestParam String compare_scids, @RequestParam String time, @RequestParam String source, @RequestParam int signature) throws IOException {

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
        this.scid=scid;
        jsonObject= documentGenerationI.get(act+"","total_cited",scid,compare_scids+"",time+"",source+"",signature);
        jsonObject1= documentGenerationI.get(act+"","paper_cited",scid,compare_scids+"",time+"",source+"",signature);
        String Time=time.toString();
        start_year=Integer.parseInt(Time.substring(10,14));
        end_year=Integer.parseInt(Time.substring(23,27));

        //解析进来的对比机构的scid
        compare_Scid=compare_scids.split(",");
        List<String> cls=new ArrayList<>();
            for(int i=0;i<compare_Scid.length;i++){
                cls.add(compare_Scid[i]);
            }
            //重要的调用部分
        MuBiao_shuju(scid);
        DuiBi_shuju(cls);


        CreateTu();
        CreateTu1();
        Map<String, Object> dataMap = new HashMap<String, Object>();

        List<Map<String, Object>> listInfo = new ArrayList<Map<String, Object>>();
        for (int i = start_year; i <= end_year; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            //目标数据
            map.put("mubiao_shuju",jsonObject.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(i + ""));
            map.put("year",i);
            //第二个表
            map.put("mubiao_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getDouble(i + ""));
            //第一个对比机构
            map.put("duibi_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getJSONObject("list").getInt(i + ""));
            //第二个表
            map.put("duibi_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1+"").getJSONObject("list").getDouble(i + ""));
            //第二个对比机构
            if(compare_Scid.length>1&&compare_Scid.length<3){
                map.put("duibi2_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi2_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i + ""));
            }
            if(compare_Scid.length>2&&compare_Scid.length<4){
                map.put("duibi2_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi2_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i + ""));
                map.put("duibi3_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi3_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getDouble(i + ""));
            }
            if(compare_Scid.length>3&&compare_Scid.length<5){
                map.put("duibi2_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi2_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i + ""));

                map.put("duibi3_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi3_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getDouble(i + ""));
                map.put("duibi4_shuju",jsonObject.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt(i + ""));
                map.put("duibi4_shuju_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getDouble(i + ""));
            }

            //

            listInfo.add(map);
        }
        //重要的地方
        Put_mubiao_shuju(scid, dataMap);
        Put_Duibi_shuju(dataMap);


        dataMap.put("listInfo",listInfo);
        //获得总被引领次最高的机构的名字
        School max_school = getSchool(cls);
        //获得中南大学被引频次最高的年份
        int max_year = getMax_year(scid);
        School max_school_paper = get_School(cls);
        int max_year_paper = getMax_year_paper(scid);


        //存放表头的动态数据
        dataMap.put("source",source);
        dataMap.put("jigou_shuming",jigou_shuming);
        //总被引频次最高的机构是：；中南大学被引频次最高的年份是年
        dataMap.put("max_school",max_school.getName());
        dataMap.put("max_year",max_year);

        //篇均被引频次最高的机构是：；中南大学篇均被引频次最高的年份是年
        dataMap.put("max_school_paper",max_school_paper.getName());
        dataMap.put("max_year_paper",max_year_paper);
        String img1 = getImgStr(filePath+"/5"+sb+".jpg");
        String img2=  getImgStr(filePath+"/6"+sb+".jpg");
        dataMap.put("shuju_tu",img1);
        dataMap.put("shuju_tu_paper",img2);

        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));

        Setting setting = new Setting("word",true);
        String settingPath = setting.getSettingPath();
        this.settingPath=settingPath;
        //文件路径
        filePath = settingPath+"";

        //文件唯一名称
        fileOnlyName = "用freemarker生成Word文档_" + sb + ".doc";

        //文件名称
        fileName = "用freemarker生成Word文档.doc";

        /** 生成word */
        WordUtil.createWord(dataMap, "被引频次对比分析.ftl", filePath, fileOnlyName);

        File tFile=new File(filePath+""+fileOnlyName);
        FileInputStream input = new FileInputStream(tFile);
        MultipartFile multipartFile = new MockMultipartFile("file", tFile.getName(), "text/plain", IOUtils.toByteArray(input));
        String fileName= documentGenerationI.input(multipartFile);
        byte[] bytes = documentGenerationI.downLoad(fileName);
        System.out.println(fileName);
        return bytes;
    }

    public int getMax_year_paper(@RequestParam int scid) {
        //获得中南大学篇均被引频次最高的年份
        double max_year_total_paper=0;
        int max_year_paper=0;
        for(int i=start_year;i<end_year;i++){
            double years_totals=jsonObject1.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(i + "");
            if(max_year_total_paper<years_totals){
                max_year_total_paper=years_totals;
                max_year_paper=i;
            }
        }
        return max_year_paper;
    }

    public School get_School(List<String> cls) {
        //获得均被引频次最高的机构
        double max_total_paper=0.00;
        String compare_scid_paper="";
        for(int i=0;i<compare_Scid.length;i++){
            Double totals=jsonObject1.getJSONObject("content").getJSONObject(cls.get(i)).getDouble("paper_cites");
            if(max_total_paper<totals){
                max_total_paper=totals;
                compare_scid_paper=cls.get(i);
            }
        }
        return schoolServiceI.findByScid(Integer.parseInt(compare_scid_paper));
    }

    public int getMax_year(@RequestParam int scid) {
        int max_year_total=0;
        int max_year=0;
        for(int i=start_year;i<end_year;i++){
            int years_totals=jsonObject.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(i + "");
            if(max_year_total<years_totals){
                max_year_total=years_totals;
                max_year=i;
            }
        }
        return max_year;
    }

    public School getSchool(List<String> cls) {
        int max_total=0;
        String compare_scid="";
        for (int i=0;i<compare_Scid.length;i++){
            int totals=jsonObject.getJSONObject("content").getJSONObject(cls.get(i)).getInt("cites");
            if(max_total<totals){
                max_total=totals;
                compare_scid=cls.get(i);
            }
        }
        return schoolServiceI.findByScid(Integer.parseInt(compare_scid));
    }

    public void Put_Duibi_shuju(Map<String, Object> dataMap) {
        //存放对比机构的数据
        //第一个对比机构的数据
        dataMap.put("duibi_jigou",duibi_jigou.getName());
        dataMap.put("duibi_total",duibi_total);
        dataMap.put("duibi_cited",duibi_cited);
        dataMap.put("duibi_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1+"").getDouble("paper_cites"));
        if(compare_Scid.length>1&&compare_Scid.length<3){
            //存放第二个对比机构的数据
            dataMap.put("duibi2_jigou",duibi2_jigou.getName());
            dataMap.put("duibi2_total",duibi2_total);
            dataMap.put("duibi2_cited",duibi2_cited);
            dataMap.put("duibi2_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getDouble("paper_cites"));

        }
        if(compare_Scid.length>2&&compare_Scid.length<4){
            //存放第二个对比机构的数据
            dataMap.put("duibi2_jigou",duibi2_jigou.getName());
            dataMap.put("duibi2_total",duibi2_total);
            dataMap.put("duibi2_cited",duibi2_cited);
            dataMap.put("duibi2_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getDouble("paper_cites"));
            //存放第三个对比机构的数据
            dataMap.put("duibi3_jigou",duibi3_jigou.getName());
            dataMap.put("duibi3_total",duibi3_total);
            dataMap.put("duibi3_cited",duibi3_cited);
            dataMap.put("duibi3_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getDouble("paper_cites"));
        }

        if(compare_Scid.length>3&&compare_Scid.length<5){
            //存放第二个对比机构的数据
            dataMap.put("duibi2_jigou",duibi2_jigou.getName());
            dataMap.put("duibi2_total",duibi2_total);
            dataMap.put("duibi2_cited",duibi2_cited);
            dataMap.put("duibi2_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getDouble("paper_cites"));
            //存放第三个对比机构的数据
            dataMap.put("duibi3_jigou",duibi3_jigou.getName());
            dataMap.put("duibi3_total",duibi3_total);
            dataMap.put("duibi3_cited",duibi3_cited);
            dataMap.put("duibi3_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getDouble("paper_cites"));
            //存放第四个对比机构的数据
            dataMap.put("duibi4_jigou",duibi4_jigou.getName());
            dataMap.put("duibi4_total",duibi4_total);
            dataMap.put("duibi4_cited",duibi4_cited);
            dataMap.put("duibi4_paper",jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4+"").getDouble("paper_cites"));
        }
    }

    public void Put_mubiao_shuju(@RequestParam int scid, Map<String, Object> dataMap) {
        //存放目标机构的数据
        dataMap.put("mubiao_jigou",mubiao_jigou.getName());

        dataMap.put("mubiao_total",mubiao_total);
        dataMap.put("mubiao_cited",mubiao_cited);
        dataMap.put("mubiao_paper",jsonObject1.getJSONObject("content").getJSONObject(scid+"").getInt("paper_cites"));
    }

    public void DuiBi_shuju(List<String> cls) {
        //获得对比机构的数据
        if(compare_Scid.length>0&&compare_Scid.length<2){
            compare_scid_value1=cls.get(0);
            duibi_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("total");
            duibi_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("cites");
            duibi_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
        }

        if(compare_Scid.length>1&&compare_Scid.length<3){
            compare_scid_value1=cls.get(0);
            compare_scid_value2=cls.get(1);
            //第一个
            duibi_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("total");
            duibi_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("cites");
            duibi_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
            //第二个
            duibi2_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("total");
            duibi2_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("cites");
            duibi2_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
        }
        if(compare_Scid.length>2&&compare_Scid.length<4){
            compare_scid_value1=cls.get(0);
            compare_scid_value2=cls.get(1);
            compare_scid_value3=cls.get(2);
            //第一个
            duibi_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("total");
            duibi_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("cites");
            duibi_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
            //第二个
            duibi2_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("total");
            duibi2_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("cites");
            duibi2_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            //第三个
            duibi3_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getInt("total");
            duibi3_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getInt("cites");
            duibi3_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3));
        }
        if(compare_Scid.length>3&&compare_Scid.length<5){
            compare_scid_value1=cls.get(0);
            compare_scid_value2=cls.get(1);
            compare_scid_value3=cls.get(2);
            compare_scid_value4=cls.get(3);
            //第一个
            duibi_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("total");
            duibi_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getInt("cites");
            duibi_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
            //第二个
            duibi2_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("total");
            duibi2_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getInt("cites");
            duibi2_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            //第三个
            duibi3_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getInt("total");
            duibi3_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getInt("cites");
            duibi3_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3));
            //第四个
            duibi4_total=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value4+"").getInt("total");
            duibi4_cited=jsonObject.getJSONObject("content").getJSONObject(compare_scid_value4+"").getInt("cites");
            duibi4_jigou=schoolServiceI.findByScid(Integer.parseInt(compare_scid_value4));
        }
    }

    public void MuBiao_shuju(@RequestParam int scid) {
        //获得目标机构的数据
        mubiao_total=jsonObject.getJSONObject("content").getJSONObject(scid+"").getInt("total");
        mubiao_cited=jsonObject.getJSONObject("content").getJSONObject(scid+"").getInt("cites");
        mubiao_jigou=schoolServiceI.findByScid(scid);
    }

    private void CreateTu() {
        CategoryDataset ds = getDataSet();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "总被引频次对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot=chart.getCategoryPlot();
        CategoryAxis domainAxis=plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis =plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out=new FileOutputStream(filePath+"/5"+sb+".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void CreateTu1() {
        CategoryDataset ds = getDataSet1();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "篇均被引频次对比分析", //图表标题
                "年", //目录轴的显示标签
                "发文量", //数值轴的显示标签
                ds, //数据集
                PlotOrientation.VERTICAL, //图表方向
                true, //是否显示图例，对于简单的柱状图必须为false
                false, //是否生成提示工具
                false);
        CategoryPlot plot=chart.getCategoryPlot();
        CategoryAxis domainAxis=plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
        ValueAxis rangeAxis =plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        FileOutputStream out = null;
        try {
            out=new FileOutputStream(filePath+"/6"+sb+".jpg");
            ChartUtilities.writeChartAsJPEG(out, 0.5f, chart, 800, 400, null);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private  CategoryDataset getDataSet()
    {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = start_year; i <= end_year; i++) {
            ds.addValue(jsonObject.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getInt(i+""), mubiao_jigou.getName()+"", i+"年");
            ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value1+"").getJSONObject("list").getInt(i+""), duibi_jigou.getName()+"", i+"年");
            if(compare_Scid.length>1&&compare_Scid.length<3){
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i+""), duibi2_jigou.getName()+"", i+"年");
            }
            if(compare_Scid.length>2&&compare_Scid.length<4){
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i+""), duibi2_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(i+""), duibi3_jigou.getName()+"", i+"年");
            }
            if(compare_Scid.length>3&&compare_Scid.length<5){
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getInt(i+""), duibi2_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getInt(i+""), duibi3_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getInt(i+""), duibi4_jigou.getName()+"", i+"年");
            }
        }
        return ds;
    }

    private  CategoryDataset getDataSet1()
    {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = start_year; i <= end_year; i++) {
            ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(scid+"").getJSONObject("list").getDouble(i+""), mubiao_jigou.getName()+"", i+"年");
            ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value1+"").getJSONObject("list").getDouble(i+""), duibi_jigou.getName()+"", i+"年");
            if(compare_Scid.length>1&&compare_Scid.length<3){
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i+""), duibi2_jigou.getName()+"", i+"年");
            }
            if(compare_Scid.length>2&&compare_Scid.length<4){
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i+""), duibi2_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getDouble(i+""), duibi3_jigou.getName()+"", i+"年");
            }
            if(compare_Scid.length>3&&compare_Scid.length<5){
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value2+"").getJSONObject("list").getDouble(i+""), duibi2_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value3+"").getJSONObject("list").getDouble(i+""), duibi3_jigou.getName()+"", i+"年");
                ds.addValue(jsonObject1.getJSONObject("content").getJSONObject(compare_scid_value4+"").getJSONObject("list").getDouble(i+""), duibi4_jigou.getName()+"", i+"年");
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
    /**
     *
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

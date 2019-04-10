package com.wd.cloud.reportanalysis.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.*;

/**
 * Created by zhouhs on 2017/1/10.
 */

@SuppressWarnings("serial")
@Controller
public class WordAction {


    //    @RequestMapping("/testmap")
//    @ResponseBody
//    public JSONObject test(@PathVariable String act,@PathVariable String table,@PathVariable int scid,@PathVariable String compare_scids,@PathVariable String time,@PathVariable String source,@PathVariable int signature){
//        // Map<String,Object> map=new HashMap<>();
//        JSONObject jsonObject=new JSONObject();
//        jsonObject= documentGenerationI.get("act","table",scid,"compare_scids",time+"","source",signature);
//        return jsonObject;
//    }
    JSONObject lists = null;
    JSONObject lists1 = null;
    JSONObject lists2 = null;
    School mubiao_jigou = null;
    School duibi_jigou = null;
    int start_year = 0;
    int end_year = 0;
    String jigou_shuming = "";
    //第二个对比机构
    JSONObject content2 = null;
    int total2 = 0;
    String[] compare_Scid = null;
    School duibi_jigou1 = null;
    //第三个对比机构
    JSONObject content3 = null;
    int total3 = 0;
    JSONObject lists3 = null;
    School duibi_jigou2 = null;
    //第四个对比机构
    JSONObject content4 = null;
    int total4 = 0;
    JSONObject lists4 = null;
    School duibi_jigou3 = null;
    //文件的相对路径
    String settingPath = "";
    StringBuffer sb = null;
    @Autowired
    private DocumentGenerationI documentGenerationI;
    @Autowired
    private SchoolServiceI schoolServiceI;
    private String filePath = settingPath + ""; //文件路径
    private String fileName; //文件名称
    private String fileOnlyName; //文件唯一名称

    @RequestMapping("/hut")
    public ResponseEntity createWord(@RequestParam String act, @RequestParam String table, @RequestParam int scid, @RequestParam String compare_scids, @RequestParam String time, @RequestParam String source, @RequestParam int signature) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject = documentGenerationI.get(act + "", table + "", scid, compare_scids + "", time + "", source + "", signature);
        String Time = time.toString();
        //获得起始时间
        start_year = Integer.parseInt(Time.substring(10, 14));
        //String start_year1=Time.substring(10,14);
        //获得结束时间
        end_year = Integer.parseInt(Time.substring(23, 27));
        //String end_year1=Time.substring(23,27);
        //获得机构署名
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
        JSONObject names = jsonObject.getJSONObject("content");

        //JSONArray content=names.getJSONArray("9");
        //System.out.println(content);
        //目标机构
        JSONObject content = names.getJSONObject(scid + "");
        //目标机构的学校名称
        mubiao_jigou = schoolServiceI.findByScid(Integer.parseInt(scid + ""));
        //获得对比机构的学校和信息
        compare_Scid = compare_scids.split(",");
        List<String> cls = new ArrayList<>();
        String compare_scid_value = "";
        String compare_scid_value1 = "";
        String compare_scid_value2 = "";
        String compare_scid_value3 = "";
        if (compare_Scid.length > 0 && compare_Scid.length < 2) {
            for (int i = 0; i < compare_Scid.length; i++) {
                cls.add(compare_Scid[i]);
                compare_scid_value = cls.get(0);
            }
        } else if (compare_Scid.length > 1 && compare_Scid.length < 3) {
            for (int i = 0; i < compare_Scid.length; i++) {
                cls.add(compare_Scid[i]);
            }
            compare_scid_value = cls.get(0);
            compare_scid_value1 = cls.get(1);
            content2 = names.getJSONObject(compare_scid_value1);
            total2 = content2.getInt("total");
            lists2 = content2.getJSONObject("list");
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));
        } else if (compare_Scid.length > 2 && compare_Scid.length < 4) {
            for (int i = 0; i < compare_Scid.length; i++) {
                cls.add(compare_Scid[i]);
            }
            compare_scid_value = cls.get(0);
            compare_scid_value1 = cls.get(1);
            compare_scid_value2 = cls.get(2);
            content2 = names.getJSONObject(compare_scid_value1);
            total2 = content2.getInt("total");
            lists2 = content2.getJSONObject("list");
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));

            content3 = names.getJSONObject(compare_scid_value2);
            total3 = content3.getInt("total");
            lists3 = content3.getJSONObject("list");
            duibi_jigou2 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
        } else {
            for (int i = 0; i < compare_Scid.length; i++) {
                cls.add(compare_Scid[i]);
            }
            compare_scid_value = cls.get(0);
            compare_scid_value1 = cls.get(1);
            compare_scid_value2 = cls.get(2);
            compare_scid_value3 = cls.get(3);
            content2 = names.getJSONObject(compare_scid_value1);
            total2 = content2.getInt("total");
            lists2 = content2.getJSONObject("list");
            duibi_jigou1 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value1));

            content3 = names.getJSONObject(compare_scid_value2);
            total3 = content3.getInt("total");
            lists3 = content3.getJSONObject("list");
            duibi_jigou2 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value2));
            content4 = names.getJSONObject(compare_scid_value3);
            total4 = content4.getInt("total");
            lists4 = content4.getJSONObject("list");
            duibi_jigou3 = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value3));
        }
        //对比机构
        JSONObject content1 = names.getJSONObject(compare_scid_value);
        duibi_jigou = schoolServiceI.findByScid(Integer.parseInt(compare_scid_value));
        int total = content.getInt("total");
        int total1 = content1.getInt("total");
        Iterator<String> it = names.keys();
        int compare_total = 0;
        String compare_scid = "";
        while (it.hasNext()) {
            String key = it.next();
            for (int i = 0; i < key.length(); i++) {
                int total5 = names.getJSONObject(key).getInt("total");
                if (compare_total < total5) {
                    compare_total = total5;
                    compare_scid = key;
                }
            }

        }
        //获得总发文量最高机构的学校
        School byScid = schoolServiceI.findByScid(Integer.parseInt(compare_scid));


        lists = content.getJSONObject("list");
        lists1 = content1.getJSONObject("list");

        // System.out.println("lists.............................................."+lists);
//        int shuju_2008=lists.getInt("2008");
        //获得中南大学发文量最多的年份
        Iterator<String> it1 = lists.keys();
        int compare_total1 = 0;
        String compare_year = "";
        while (it1.hasNext()) {
            String key = it1.next();
            for (int i = 0; i < key.length(); i++) {
                int total5 = lists.getInt(key + "");
                if (compare_total1 < total5) {
                    compare_total1 = total5;
                    compare_year = key;
                }
                //System.out.println("hahahahha-------------------------------------------------------hahahahahahahahahahah"+total5);
            }
        }

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
            out = new FileOutputStream(filePath + "/1" + sb + ".jpg");
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
        /** 用于组装word页面需要的数据 */
        Map<String, Object> dataMap = new HashMap<String, Object>();

        /** 组装数据 */
        dataMap.put("total", total);
        List<Map<String, Object>> listInfo = new ArrayList<Map<String, Object>>();
        for (int i = start_year; i <= end_year; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("shuju_2008", lists.getInt(i + ""));
            map.put("shuju_2009", lists1.getInt(i + ""));
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                map.put("shuju_2010", lists2.getInt(i + ""));
            }
            //第三个对比机构数据
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                map.put("shuju_2010", lists2.getInt(i + ""));
                map.put("shuju_2011", lists3.getInt(i + ""));
            }
            //第四个对比机构
            if (compare_Scid.length > 3 && compare_Scid.length < 5) {
                map.put("shuju_2010", lists2.getInt(i + ""));
                map.put("shuju_2011", lists3.getInt(i + ""));
                map.put("shuju_2012", lists4.getInt(i + ""));
            }
            map.put("year", i);
            listInfo.add(map);
        }
//        /** 组装每一列的数据 */
//        List<Map<String,Object>> listInfo1=new ArrayList<>();
//        for(int i=start_year;i<end_year;i++){
//            Map<String,Object> map=new HashMap<>();
//            //封装的为新加入的每一个学校的数据
//            map.put("lie_shuju",123);
//            listInfo1.add(map);
//        }
        dataMap.put("zuigao_jigou", byScid.getName());
        dataMap.put("mubiao_jigou", mubiao_jigou.getName());
        //第一个对比机构
        dataMap.put("duibi_jigou", duibi_jigou.getName());
        //第二个对比机构

        if (compare_Scid.length > 1 && compare_Scid.length < 3) {
            dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            dataMap.put("total2", total2);
            dataMap.put("content2", content2);
        }
        //第三个对比机构
        if (compare_Scid.length > 2 && compare_Scid.length < 4) {
            //分割线
            dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            dataMap.put("total2", total2);
            dataMap.put("content2", content2);
            //分割线
            dataMap.put("duibi_jigou2", duibi_jigou2.getName());
            dataMap.put("content3", content3);
            dataMap.put("total3", total3);
        }
        //第四个对比机构
        if (compare_Scid.length > 3 && compare_Scid.length < 5) {
            //分割线
            dataMap.put("duibi_jigou1", duibi_jigou1.getName());
            dataMap.put("total2", total2);
            dataMap.put("content2", content2);
            //分割线
            dataMap.put("duibi_jigou2", duibi_jigou2.getName());
            dataMap.put("content3", content3);
            dataMap.put("total3", total3);
            //分割线
            dataMap.put("duibi_jigou3", duibi_jigou3.getName());
            dataMap.put("content4", content4);
            dataMap.put("total4", total4);
        }


        dataMap.put("compare_year", compare_year);
        dataMap.put("source", source);
        dataMap.put("jigou_shuming", jigou_shuming);
        dataMap.put("listInfo", listInfo);
        String img1 = getImgStr(filePath + "/1" + sb + ".jpg");
        dataMap.put("shuju_tu", img1);

        dataMap.put("weidu", "weidukeji");

        dataMap.put("total1", total1);

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
        WordUtil.createWord(dataMap, "数据3.ftl", filePath, fileOnlyName);


        File tFile = new File(filePath + "" + fileOnlyName);
        FileInputStream input = new FileInputStream(tFile);
        MultipartFile multipartFile = new MockMultipartFile("file", tFile.getName(), "text/plain", IOUtils.toByteArray(input));
        String fileName = documentGenerationI.input(multipartFile);
        System.out.println(fileName);
        //JSONObject filename2=rngNiuBiServer.download(fileName);
        // System.out.println(filename2+"----------------------------------------------------------------------");
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(fileName, map.getClass());
        String filename = (String) map.get("file");
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


        // return "createWordSuccess";


    }

    private CategoryDataset getDataSet() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = start_year; i <= end_year; i++) {
            ds.addValue(lists.getInt(i + ""), mubiao_jigou.getName() + "", i + "年");
            ds.addValue(lists1.getInt(i + ""), duibi_jigou.getName() + "", i + "年");
            if (compare_Scid.length > 1 && compare_Scid.length < 3) {
                ds.addValue(lists2.getInt(i + ""), duibi_jigou1.getName() + "", i + "年");
            }
            if (compare_Scid.length > 2 && compare_Scid.length < 4) {
                ds.addValue(lists2.getInt(i + ""), duibi_jigou1.getName() + "", i + "年");
                ds.addValue(lists3.getInt(i + ""), duibi_jigou2.getName() + "", i + "年");
            } else {
                ds.addValue(lists2.getInt(i + ""), duibi_jigou1.getName() + "", i + "年");
                ds.addValue(lists3.getInt(i + ""), duibi_jigou2.getName() + "", i + "年");
                ds.addValue(lists4.getInt(i + ""), duibi_jigou3.getName() + "", i + "年");
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

//    @RequestMapping(value={"/download"},method = {RequestMethod.GET})
//    public  String download(MultipartFile file){
//        File tFile=new File(filePath+""+fileOnlyName);
//        FileSystemResource fs=new FileSystemResource(tFile);
//        String fileName= rngNiuBiServer.input(fs);
//        System.out.println(fileName);
//        return fileName;
//    }

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

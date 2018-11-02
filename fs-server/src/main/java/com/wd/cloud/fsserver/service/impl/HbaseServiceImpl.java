package com.wd.cloud.fsserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.model.HbaseObjModel;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/10/31
 * @Description:
 */
@Service("hbaseService")
public class HbaseServiceImpl implements HbaseService {
    private static final Log log = LogFactory.get();
    @Autowired
    GlobalConfig globalConfig;
    @Autowired
    HbaseTemplate hbaseTemplate;

    @Autowired
    HBaseAdmin hBaseAdmin;
    @Autowired
    FileService fileService;
    @Autowired
    UploadRecordService uploadRecordService;

    @Override
    public void saveToHbase(String tableName, String unid, MultipartFile file) throws Exception {
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(file.getOriginalFilename())
                .setRowKey(unid.getBytes())
                .setValue(file.getBytes());
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(String tableName, String unid, File file) throws Exception {
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(unid.getBytes())
                .setValue(FileUtil.readBytes(file));
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(String tableName, String unid, byte[] fileByte, String fileName) throws Exception {
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(fileName)
                .setRowKey(unid.getBytes())
                .setValue(fileByte);
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(String tableName, File file) throws Exception {
        String unid = FileUtil.buildFileUuid(tableName, FileUtil.fileMd5(file));
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(unid.getBytes())
                .setValue(FileUtil.readBytes(file));
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(HbaseObjModel hbaseObjModel) throws Exception {
        log.info("正在上传文件：{}至HBASE...", hbaseObjModel.getFileName());
        hbaseTemplate.execute(hbaseObjModel.getTableName(), (hTableInterface) -> {
            Put put = new Put(hbaseObjModel.getRowKey());
            put.addColumn(hbaseObjModel.getFamily(), hbaseObjModel.getQualifier(), hbaseObjModel.getValue());
            hTableInterface.put(put);
            log.info("文件：{} 已保存HBASE", hbaseObjModel.getFileName());
            return true;
        });
    }

    @Override
    public byte[] getFileFromHbase(String tableName, String unid) {
        return hbaseTemplate.get(tableName, unid, new RowMapper<byte[]>() {
            @Override
            public byte[] mapRow(Result result, int i) {
                byte[] fileByte = null;
                List<Cell> cells = result.listCells();
                if (cells != null) {
                    Cell cell = cells.stream().findFirst().get();
                    fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    log.info("从hbase读取文件unid：{}，size：{} byte", unid, fileByte.length);
                }
                return fileByte;
            }
        });
    }

    @Override
    public boolean deleteFileFromHbase(String tableName, String rowKey) {
        log.info("正在删除bahse文件: {} ...", rowKey);
        hbaseTemplate.delete(tableName, rowKey, "cf");
        log.info("删除bahse文件: {} 成功", rowKey);
        return true;
    }

    @Override
    public boolean hfToUploadRecord(String tableName) {
        hbaseTemplate.find(tableName, new Scan(), new RowMapper<String>() {
            @Override
            public String mapRow(Result result, int i) {
                List<Cell> cells = result.listCells();
                if (cells != null) {
                    cells.forEach(cell -> {
                        String fileName = null;
                        try {
                            fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                            byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                            File file = FileUtil.saveToDisk(FileUtil.getTmpDirPath(), fileName, fileByte);
                            String md5 = FileUtil.fileMd5(file);
                            fileName = FileUtil.buildFileName(fileName, file);
                            uploadRecordService.save(tableName, fileName, md5, file);
                            FileUtil.copy(file, new File(globalConfig.getRootPath() + tableName, fileName), true);
                        } catch (Exception e) {
                            log.error(e, "fileName:{}", fileName);
                        }
                    });
                }
                return "ok";
            }
        });
        return true;
    }

    @Override
    public void dropTable(String tableName) throws IOException {
        hBaseAdmin.disableTable(tableName);
        hBaseAdmin.deleteTable(tableName);
    }

    @Override
    public void createTable(String tableName) {
//        HTableDescriptor hTableDescriptor  = new HTableDescriptor()
//        hBaseAdmin.createTable().disableTable(tableName);
//        hBaseAdmin.deleteTable(tableName);
    }
}

package com.wd.cloud.fsserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.model.TableModel;
import com.wd.cloud.fsserver.service.FileService;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.service.UploadRecordService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author He Zhigang
 * @date 2018/10/31
 * @Description:
 */
@Component("hbaseService")
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
    public void saveToHbase(String tableName, String md5, MultipartFile file) throws Exception {
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getOriginalFilename())
                .setRowKey(md5)
                .setValue(file.getBytes());
        saveToHbase(tableModel);
    }

    @Override
    public void saveToHbase(String tableName, String md5, File file) throws Exception {
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(md5)
                .setValue(FileUtil.readBytes(file));
        saveToHbase(tableModel);
    }

    @Override
    public void saveToHbase(String tableName, String md5, byte[] fileByte, String fileName) throws Exception {
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(fileName)
                .setRowKey(md5)
                .setValue(fileByte);
        saveToHbase(tableModel);
    }

    @Override
    public void saveToHbase(String tableName, File file) throws Exception {
        String md5 = FileUtil.fileMd5(file);
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(md5)
                .setValue(FileUtil.readBytes(file));
        saveToHbase(tableModel);
    }

    @Override
    public void saveToHbase(TableModel tableModel) throws Exception {
        log.info("正在上传文件：{}至HBASE...", tableModel.getFileName());
        hbaseTemplate.execute(tableModel.getTableName(), (hTableInterface) -> {
            Put put = new Put(tableModel.getRowKey().getBytes());
            put.addColumn(tableModel.getFamily().getBytes(),
                    tableModel.getQualifier().getBytes(),
                    tableModel.getValue());
            hTableInterface.put(put);
            log.info("文件：{} 已保存HBASE", tableModel.getFileName());
            return true;
        });
    }

    @Override
    public byte[] getFileFromHbase(String tableName, String md5) {
        return hbaseTemplate.get(tableName, md5, new RowMapper<byte[]>() {
            @Override
            public byte[] mapRow(Result result, int i) {
                byte[] fileByte = null;
                List<Cell> cells = result.listCells();
                if (cells != null) {
                    Cell cell = cells.stream().findFirst().get();
                    fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    log.info("从hbase读取文件md5：{}，size：{} byte", md5, fileByte.length);
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
    public int hfToUploadRecord(String tableName) {
        AtomicInteger count = new AtomicInteger();
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
                            File file = FileUtil.saveToDisk(globalConfig.getRootPath() + tableName, fileName, fileByte);
                            uploadRecordService.save(tableName, fileName, file);
                            count.getAndIncrement();
                        } catch (Exception e) {
                            log.error(e, "fileName:{}", fileName);
                        }
                    });
                }
                return "ok";
            }
        });
        return count.get();
    }

    @Override
    public void dropTable(String tableName) throws IOException {
        hBaseAdmin.disableTable(tableName);
        hBaseAdmin.deleteTable(tableName);
    }

    @Override
    public void createTable(String tableName) throws IOException {
        TableModel tableModel = new TableModel();
        tableModel.setTableName(tableName);
        HTableDescriptor table = new HTableDescriptor(tableName);
        table.addFamily(new HColumnDescriptor(tableModel.getFamily()));
        hBaseAdmin.createTable(table);
    }
}

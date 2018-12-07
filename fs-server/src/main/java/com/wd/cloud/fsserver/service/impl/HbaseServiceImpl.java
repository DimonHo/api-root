package com.wd.cloud.fsserver.service.impl;

import cn.hutool.core.lang.Console;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.config.GlobalConfig;
import com.wd.cloud.fsserver.entity.UploadRecord;
import com.wd.cloud.fsserver.model.FileModel;
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
import java.util.ArrayList;
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
    public boolean saveToHbase(String tableName, String md5, MultipartFile file) throws Exception {
        String fileMd5Name = FileUtil.buildFileMd5Name(file, md5);
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getOriginalFilename())
                .setRowKey(fileMd5Name)
                .setValue(file.getBytes());
        return saveToHbase(tableModel);
    }

    @Override
    public boolean saveToHbase(String tableName, String md5, File file) throws Exception {
        String fileMd5Name = FileUtil.buildFileMd5Name(file, md5);
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(fileMd5Name)
                .setValue(FileUtil.readBytes(file));
        return saveToHbase(tableModel);
    }


    @Override
    public boolean saveToHbase(String tableName, File file) throws Exception {
        String md5 = FileUtil.fileMd5(file);
        String fileMd5Name = FileUtil.buildFileMd5Name(file, md5);
        TableModel tableModel = TableModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(fileMd5Name)
                .setValue(FileUtil.readBytes(file));
        return saveToHbase(tableModel);
    }

    @Override
    public boolean saveToHbase(TableModel tableModel) {
        boolean uploadSuccess = false;
        log.info("正在上传文件：{}至HBASE...", tableModel.getFileName());
        try {
            uploadSuccess = hbaseTemplate.execute(tableModel.getTableName(), (hTableInterface) -> {
                Put put = new Put(tableModel.getRowKey().getBytes());
                put.addColumn(tableModel.getFamily().getBytes(),
                        tableModel.getQualifier().getBytes(),
                        tableModel.getValue());
                hTableInterface.put(put);
                log.info("文件：{} 已保存HBASE", tableModel.getFileName());
                return true;
            });
        } catch (Exception e) {
            log.error(e, "文件[{}]上传hbase失败", tableModel.getFileName());
        }
        return uploadSuccess;
    }

    @Override
    public FileModel getFileFromHbase(String tableName, String rowKey) {
        List<FileModel> fileModels = hbaseTemplate.get(tableName, rowKey, new RowMapper<List<FileModel>>() {
            @Override
            public List<FileModel> mapRow(Result result, int i) {
                List<FileModel> fileModels = new ArrayList<>();
                List<Cell> cells = result.listCells();
                if (cells != null && cells.size() > 0) {
                    cells.forEach(cell -> {
                        FileModel fileModel = new FileModel();
                        String fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        log.info("从hbase读取文件md5：{}，size：{} byte", rowKey, fileByte.length);
                        fileModel.setName(fileName).setBytes(fileByte);
                        fileModels.add(fileModel);
                    });
                }
                return fileModels;
            }
        });
        if (fileModels.size() > 1) {
            log.error("在hbase中找到了多条记录!tableName={},rowkey={}", tableName, rowKey);
        }
        return fileModels.get(0);
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
                        String newTable = tableName;
                        if ("doc-delivery".equals(tableName)) {
                            newTable = "literature";
                        }
                        try {
                            fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                            byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                            File file = FileUtil.saveToDisk(globalConfig.getRootPath() + newTable, fileName, fileByte);
                            String fileMd5Name = FileUtil.buildFileMd5Name(file);
                            // 以MD5文件名保存
                            if (!fileMd5Name.equals(file.getName())) {
                                file = FileUtil.rename(file, fileMd5Name, false, true);
                            }
                            UploadRecord uploadRecord = new UploadRecord();
                            uploadRecord.setPath(newTable)
                                    .setFileName(fileMd5Name)
                                    .setTemp(fileName)
                                    .setMd5(FileUtil.fileMd5(file))
                                    .setFileType(FileUtil.getFileType(file))
                                    .setFileSize(file.length());
                            uploadRecordService.save(uploadRecord);
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
    public void asFileFromHbase(String tableName) {
        List<String> lines = FileUtil.readUtf8Lines("/home/cloud/async.txt");

        lines.forEach(line -> {
            Console.log(line);
            hbaseTemplate.get(tableName, line, new RowMapper<String>() {
                @Override
                public String mapRow(Result result, int i) {

                    List<Cell> cells = result.listCells();
                    if (cells != null) {
                        cells.forEach(cell -> {
                            String fileName = null;
                            String newTable = tableName;
                            if ("doc-delivery".equals(tableName)) {
                                newTable = "literature";
                            }
                            try {
                                fileName = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                                byte[] fileByte = Arrays.copyOfRange(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                                File file = FileUtil.saveToDisk(globalConfig.getRootPath() + newTable, fileName, fileByte);
                                String fileMd5Name = FileUtil.buildFileMd5Name(file);
                                // 以MD5文件名保存
                                if (!fileMd5Name.equals(file.getName())) {
                                    file = FileUtil.rename(file, fileMd5Name, false, true);
                                }
                                uploadRecordService.save(newTable, fileName, file);
                            } catch (Exception e) {
                                log.error(e, "fileName:{}", fileName);
                            }
                        });
                    }
                    return "ok";
                }
            });
        });
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

package com.wd.cloud.fsserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.fsserver.model.HbaseObjModel;
import com.wd.cloud.fsserver.service.HbaseService;
import com.wd.cloud.fsserver.util.FileUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.OrderBy;
import java.io.File;
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
    HbaseTemplate hbaseTemplate;

    @Override
    public void saveToHbase(String tableName, String unid, MultipartFile file) throws Exception{
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(file.getOriginalFilename())
                .setRowKey(unid.getBytes())
                .setValue(file.getBytes());
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(String tableName, String unid, File file) throws Exception{
        HbaseObjModel hbaseObjModel = HbaseObjModel.create().setTableName(tableName)
                .setFileName(file.getName())
                .setRowKey(unid.getBytes())
                .setValue(FileUtil.readBytes(file));
        saveToHbase(hbaseObjModel);
    }

    @Override
    public void saveToHbase(String tableName, File file) throws Exception{
        String unid = FileUtil.buildFileUuid(tableName,FileUtil.fileMd5(file));
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
                    log.info("从hbase读取文件：{}，size：{} byte", unid, fileByte.length);
                }
                return fileByte;
            }
        });
    }
}

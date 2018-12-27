package com.wd.cloud.docdelivery.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author He Zhigang
 * @date 2018/11/22
 * @Description:
 */
@FeignClient(value = "fs-server",
        configuration = com.wd.cloud.apifeign.FsServerApi.MultipartSupportConfig.class,
        fallback = FsServerApi.Fallback.class)
public interface FsServerApi extends com.wd.cloud.apifeign.FsServerApi {

    @Component
    class Fallback implements FsServerApi {

        @Override
        public ResponseModel<JSONObject> checkFile(String dir, String fileMd5) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel<JSONObject> uploadFile(String dir, MultipartFile file) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel<JSONObject> uploadFiles(String dir, MultipartFile[] files) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseEntity downloadFile(String unid) {
            return ResponseEntity.status(2).build();
        }

        @Override
        public ResponseModel<byte[]> getFileByte(String unid) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel hfToUploadRecord(String tableName) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel<String> getunid(String tableName, String fileName) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}

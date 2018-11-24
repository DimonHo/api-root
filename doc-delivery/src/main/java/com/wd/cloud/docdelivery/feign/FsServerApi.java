package com.wd.cloud.docdelivery.feign;

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
        public ResponseModel uploadFile(String dir, MultipartFile file) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel uploadFiles(String dir, MultipartFile[] files) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseEntity downloadFile(String unid) {
            return null;
        }

        @Override
        public ResponseModel getFile(String unid) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel getFileByte(String unid) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel hfToUploadRecord(String tableName) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel getunid(String tableName, String fileName) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}

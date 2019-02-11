package com.wd.cloud.docdelivery.feign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2019/1/17
 * @Description:
 */
@FeignClient(value = "pdfSearch-server", fallback = PdfSearchServerApi.Fallback.class)
public interface PdfSearchServerApi {

    @PostMapping("/pdfsearch-server/searchpdf")
    public ResponseModel<String> search(@RequestBody Literature literature);

    @GetMapping("/pdfsearch-server/search/{rowkey}")
    public ResponseModel<byte[]> getFileByte(@PathVariable(value = "rowkey") String rowkey);

    @Component
    class Fallback implements PdfSearchServerApi {


        @Override
        public ResponseModel<String> search(Literature literature) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }

        @Override
        public ResponseModel<byte[]> getFileByte(String fileId) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}

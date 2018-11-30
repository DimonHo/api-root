package com.wd.cloud.apifeign;

import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.model.ResponseModel;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/10/30
 * @Description:
 */
@FeignClient(value = "fs-server",
        configuration = FsServerApi.MultipartSupportConfig.class)
public interface FsServerApi {

    @PostMapping(value = "/upload/{dir}", consumes = "multipart/form-data")
    ResponseModel<JSONObject> uploadFile(@PathVariable(value = "dir") String dir,
                                                @RequestPart(value = "file") MultipartFile file);

    @PostMapping(value = "/upload/{dir}", consumes = "multipart/form-data")
    ResponseModel<JSONObject> uploadFiles(@PathVariable(value = "dir") String dir,
                                                 @RequestPart(value = "files") MultipartFile[] files);

    @GetMapping(value = "/download/{unid}")
    ResponseEntity downloadFile(@PathVariable(value = "unid") String unid);

    @GetMapping("/file/{unid}")
    ResponseModel<File> getFile(@PathVariable(value = "unid") String unid);

    @GetMapping("/byte/{unid}")
    ResponseModel<byte[]> getFileByte(@PathVariable(value = "unid") String unid);

    @GetMapping("/async")
    ResponseModel hfToUploadRecord(@RequestParam(value = "tableName") String tableName);

    @GetMapping("/getunid")
    ResponseModel<String> getunid(@RequestParam(value = "tableName") String tableName, @RequestParam(value = "fileName") String fileName);

    class MultipartSupportConfig {
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }


        @Bean
        public Decoder feignDecoder() {
            final List<HttpMessageConverter<?>> springConverters = messageConverters.getObject().getConverters();
            final List<HttpMessageConverter<?>> decoderConverters
                    = new ArrayList<HttpMessageConverter<?>>(springConverters.size() + 1);

            decoderConverters.addAll(springConverters);
            //decoderConverters.add(new ByteArrayHttpMessageConverter());
            final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);

            return new SpringDecoder(new ObjectFactory<HttpMessageConverters>() {
                @Override
                public HttpMessageConverters getObject() {
                    return httpMessageConverters;
                }
            });
        }
    }
}
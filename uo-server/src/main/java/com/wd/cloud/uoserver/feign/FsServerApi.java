package com.wd.cloud.uoserver.feign;


import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.enums.StatusEnum;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@FeignClient(value = "fs-server",
        configuration = FsServerApi.MultipartSupportConfig.class,
        fallback = FsServerApi.Fallback.class)
public interface FsServerApi {

    @PostMapping(value = "/upload/{dir}", consumes = "multipart/form-data")
    ResponseModel<JSONObject> uploadFile(@PathVariable(value = "dir") String dir,
                                         @RequestPart(value = "file") MultipartFile file);


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
            final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);
            return new SpringDecoder(new ObjectFactory<HttpMessageConverters>() {
                @Override
                public HttpMessageConverters getObject() {
                    return httpMessageConverters;
                }
            });
        }
    }

    @Component("fsServerApi")
    class Fallback implements FsServerApi {

        @Override
        public ResponseModel<JSONObject> uploadFile(String dir, MultipartFile file) {
            return ResponseModel.fail(StatusEnum.FALL_BACK);
        }
    }
}

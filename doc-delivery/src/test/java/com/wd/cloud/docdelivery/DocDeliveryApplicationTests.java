package com.wd.cloud.docdelivery;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.netflix.discovery.converters.Auto;
import com.wd.cloud.apifeign.FsServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import io.swagger.annotations.ApiImplicitParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DocDeliveryApplicationTests {

    @Test
    public void contextLoads() {

    }

}

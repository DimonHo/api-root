package com.wd.cloud.reportanalysis.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author He Zhigang
 * @date 2019/1/16
 * @Description:
 */
@FeignClient(value = "fs-server", fallback = FsServerApi.Fallback.class)
public interface FsServerApi {

    class Fallback implements FsServerApi {

    }
}

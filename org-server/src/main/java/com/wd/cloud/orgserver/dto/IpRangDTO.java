package com.wd.cloud.orgserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@Data
@Accessors(chain = true)
public class IpRangDTO {

    private String begin;

    private String end;
}

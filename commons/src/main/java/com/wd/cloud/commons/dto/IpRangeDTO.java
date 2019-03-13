package com.wd.cloud.commons.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description:
 */
@Data
@Accessors(chain = true)
public class IpRangeDTO implements Serializable {

    private String begin;
    private String end;

}

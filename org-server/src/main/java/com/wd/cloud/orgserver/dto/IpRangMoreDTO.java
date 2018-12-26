package com.wd.cloud.orgserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@Data
@Accessors(chain = true)
public class IpRangMoreDTO {
    private Long orgId;
    private String orgName;
    List<IpRangDTO> ipRang;
}

package com.wd.cloud.docdelivery.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DocFileDTO {

    private String fileId;

    private boolean reusing;
}

package com.wd.cloud.uoserver.dto;


import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class DepartmentDTO implements Serializable {
    private Long id;

    private String name;

    private Long orgId;

    private String orgName;

    private int number;
}

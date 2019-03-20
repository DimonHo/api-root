package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构院系表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "department",uniqueConstraints = {@UniqueConstraint(columnNames={"name", "org_flag"})})
public class Department extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    /**
     * 所属上级院系
     */
    private Long pid;

    /**
     * 院系名称
     */
    private String name;

    @Column(name = "org_flag")
    private String orgFlag;
}

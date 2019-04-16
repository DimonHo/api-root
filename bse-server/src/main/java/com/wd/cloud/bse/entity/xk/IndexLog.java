package com.wd.cloud.bse.entity.xk;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "index_log")
public class IndexLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "datas")
    private String datas;
    @Column(name = "operator")
    private String operator;
    @Column(name = "op_time")
    private Date opTime = new Date();

    public IndexLog() {
    }

    public IndexLog(String operator, String datas) {
        this.operator = operator;
        this.datas = datas;
    }
    ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

}

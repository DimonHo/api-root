package org.bse.server.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wd_cu_scholar")
public class Scholar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "school")
    private String school;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "school_smail")
    private String schoolSmail;
    

//    @Column(name = "province")
//    private String province;
//
//    @Column(name = "city")
//    private String city;
//
//    @Column(name = "level")
//    private String level;
//
//    @Column(name = "dept")
//    private String dept;
//
//    @Column(name = "remark")
//    private String remark;
//
//    /**
//     * 学校的应为名称
//     */
//    @Column(name = "index_name")
//    private String indexName;
//
//    @Column(name = "update_time")
//    private Date updateTime;
//
//    @Column(name = "code")
//    private String code;

    @Column(name = "scid")
    private Integer scid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScid() {
		return scid;
	}

	public void setScid(Integer scid) {
		this.scid = scid;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSchoolSmail() {
		return schoolSmail;
	}

	public void setSchoolSmail(String schoolSmail) {
		this.schoolSmail = schoolSmail;
	}

    /**
     * 对应的ESI机构
     */
//    @Column(name = "esi_inst")
//    private String esiInst;
//
//    /**
//     * 对应的Incites机构
//     */
//    @Column(name = "incites_inst")
//    private String incitesInst;


}

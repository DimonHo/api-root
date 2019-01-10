package com.wd.cloud.bse.entity.school;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "university")
public class University {
	
	@Id
	 @Column(name = "ID")
    private Integer id;

    @Column(name = "Name")
    private String name;
    
    @Column(name = "Shorthand")
    private String shorthand;

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

	public String getShorthand() {
		return shorthand;
	}

	public void setShorthand(String shorthand) {
		this.shorthand = shorthand;
	}

	

}

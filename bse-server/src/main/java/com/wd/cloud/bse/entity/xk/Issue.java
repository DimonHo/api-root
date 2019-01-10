package com.wd.cloud.bse.entity.xk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "issue")
public class Issue {
	
	@Id
    @Column(name = "issue")
    private String issue;
    
    @Column(name = "esi_issue")
    private String esiIssue;
    
    @Column(name = "wos_issue")
    private String wosIssue;
    
    @Column(name = "rangdate")
    private String rangdate;

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getEsiIssue() {
		return esiIssue;
	}

	public void setEsiIssue(String esiIssue) {
		this.esiIssue = esiIssue;
	}

	public String getWosIssue() {
		return wosIssue;
	}

	public void setWosIssue(String wosIssue) {
		this.wosIssue = wosIssue;
	}

	public String getRangdate() {
		return rangdate;
	}

	public void setRangdate(String rangdate) {
		this.rangdate = rangdate;
	}
    
    

}

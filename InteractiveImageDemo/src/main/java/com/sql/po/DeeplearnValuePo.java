package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;


/**
 * The persistent class for the deeplearn_value database table.
 * 
 */
@Entity
@Table(name="deeplearn_value")
@DynamicInsert
@DynamicUpdate
public class DeeplearnValuePo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "`id`")
	private Integer id;
	@Column(name = "`time`")
	private Timestamp time;

	@Column(name = "`value`")
	private String value;

	public DeeplearnValuePo() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
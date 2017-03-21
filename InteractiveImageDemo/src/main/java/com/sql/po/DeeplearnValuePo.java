package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the deeplearn_value database table.
 * 
 */
@Entity
@Table(name="deeplearn_value")

public class DeeplearnValuePo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	@Column(name="`form_value`")
	private String formValue;
	@Column(name="min_value")
	private double minValue;
	@Column(name="`time`")
	private Timestamp time;
	@Column(name="`value`")
	private String value;

	public DeeplearnValuePo() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormValue() {
		return this.formValue;
	}

	public void setFormValue(String formValue) {
		this.formValue = formValue;
	}

	public double getMinValue() {
		return this.minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
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
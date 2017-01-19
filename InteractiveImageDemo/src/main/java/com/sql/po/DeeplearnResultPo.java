package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The persistent class for the deeplearn_result database table.
 * 
 */
@Entity
@Table(name="deeplearn_result")
@DynamicInsert
@DynamicUpdate
public class DeeplearnResultPo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;
	@Column(name="`pa`")
	private float pa;
	@Column(name="`pb`")
	private float pb;
	@Column(name="`pc`")
	private float pc;

	@Column(name="`pd`")
	private String pd;
	@Column(name="`ra`")
	private float ra;
	@Column(name="`rb`")
	private float rb;
	@Column(name="`rc`")
	private float rc;
	@Column(name="`rd`")
	private float rd;

	@Column(name="result_per")
	private float resultPer;

	@Column(name="`result_per_limt`")
	private float resultPerLimt;

	@Column(name="`success_per`")
	private float successPer;

	public DeeplearnResultPo() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getPa() {
		return this.pa;
	}

	public void setPa(float pa) {
		this.pa = pa;
	}

	public float getPb() {
		return this.pb;
	}

	public void setPb(float pb) {
		this.pb = pb;
	}

	public float getPc() {
		return this.pc;
	}

	public void setPc(float pc) {
		this.pc = pc;
	}

	public String getPd() {
		return this.pd;
	}

	public void setPd(String pd) {
		this.pd = pd;
	}

	public float getRa() {
		return this.ra;
	}

	public void setRa(float ra) {
		this.ra = ra;
	}

	public float getRb() {
		return this.rb;
	}

	public void setRb(float rb) {
		this.rb = rb;
	}

	public float getRc() {
		return this.rc;
	}

	public void setRc(float rc) {
		this.rc = rc;
	}

	public float getRd() {
		return this.rd;
	}

	public void setRd(float rd) {
		this.rd = rd;
	}

	public float getResultPer() {
		return this.resultPer;
	}

	public void setResultPer(float resultPer) {
		this.resultPer = resultPer;
	}

	public float getResultPerLimt() {
		return this.resultPerLimt;
	}

	public void setResultPerLimt(float resultPerLimt) {
		this.resultPerLimt = resultPerLimt;
	}

	public float getSuccessPer() {
		return this.successPer;
	}

	public void setSuccessPer(float successPer) {
		this.successPer = successPer;
	}

}
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
	private double pa;
	@Column(name="`pb`")
	private double pb;
	@Column(name="`pc`")
	private double pc;

	@Column(name="`pd`")
	private String pd;
	@Column(name="`ra`")
	private double ra;
	@Column(name="`rb`")
	private double rb;
	@Column(name="`rc`")
	private double rc;
	@Column(name="`rd`")
	private double rd;

	@Column(name="result_per")
	private double resultPer;

	@Column(name="`result_per_limt`")
	private double resultPerLimt;

	@Column(name="`success_per`")
	private double successPer;

	public DeeplearnResultPo() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getPa() {
		return this.pa;
	}

	public void setPa(double pa) {
		this.pa = pa;
	}

	public double getPb() {
		return this.pb;
	}

	public void setPb(double pb) {
		this.pb = pb;
	}

	public double getPc() {
		return this.pc;
	}

	public void setPc(double pc) {
		this.pc = pc;
	}

	public String getPd() {
		return this.pd;
	}

	public void setPd(String pd) {
		this.pd = pd;
	}

	public double getRa() {
		return this.ra;
	}

	public void setRa(double ra) {
		this.ra = ra;
	}

	public double getRb() {
		return this.rb;
	}

	public void setRb(double rb) {
		this.rb = rb;
	}

	public double getRc() {
		return this.rc;
	}

	public void setRc(double rc) {
		this.rc = rc;
	}

	public double getRd() {
		return this.rd;
	}

	public void setRd(double rd) {
		this.rd = rd;
	}

	public double getResultPer() {
		return this.resultPer;
	}

	public void setResultPer(double resultPer) {
		this.resultPer = resultPer;
	}

	public double getResultPerLimt() {
		return this.resultPerLimt;
	}

	public void setResultPerLimt(double resultPerLimt) {
		this.resultPerLimt = resultPerLimt;
	}

	public double getSuccessPer() {
		return this.successPer;
	}

	public void setSuccessPer(double successPer) {
		this.successPer = successPer;
	}

}
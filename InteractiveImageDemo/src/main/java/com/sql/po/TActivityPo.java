package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the t_activity database table.
 * 
 */
@Entity
@Table(name = "t_activity")

public class TActivityPo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	private Integer id;

	@Column(name = "`date_end`")
	private Timestamp dateEnd;

	@Column(name = "`date_start`")
	private Timestamp dateStart;

	@Column(name = "`desc`")
	private String desc;
	@Column(name = "`enable`")
	private byte enable;

	@Column(name = "`only_vip`")
	private byte onlyVip;

	@Lob
	@Column(name = "`other_info`")
	private String otherInfo;
	@Column(name = "`priority`")
	private int priority;

	@Column(name = "`time_end`")
	private float timeEnd;

	@Column(name = "`time_start`")
	private float timeStart;
	
	@Column(name = "`type`")
	private String type;

	@Column(name = "`weeks`")
	private String weeks;

	public TActivityPo() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(Timestamp dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Timestamp getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(Timestamp dateStart) {
		this.dateStart = dateStart;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public byte getEnable() {
		return this.enable;
	}

	public void setEnable(byte enable) {
		this.enable = enable;
	}

	public byte getOnlyVip() {
		return this.onlyVip;
	}

	public void setOnlyVip(byte onlyVip) {
		this.onlyVip = onlyVip;
	}

	public String getOtherInfo() {
		return this.otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getTimeEnd() {
		return this.timeEnd;
	}

	public void setTimeEnd(float timeEnd) {
		this.timeEnd = timeEnd;
	}

	public float getTimeStart() {
		return this.timeStart;
	}

	public void setTimeStart(float timeStart) {
		this.timeStart = timeStart;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWeeks() {
		return this.weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

}
package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The persistent class for the data_base database table.
 * 
 */
@Entity
@Table(name="data_base")
@DynamicInsert
@DynamicUpdate
public class DataBasePo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DataBasePoPK id;

	private double end;

	private double start;

	private double turnover;

	@Column(name="turnover_volume")
	private double turnoverVolume;

	public DataBasePo() {
	}

	public DataBasePoPK getId() {
		return this.id;
	}

	public void setId(DataBasePoPK id) {
		this.id = id;
	}

	public double getEnd() {
		return this.end;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public double getStart() {
		return this.start;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getTurnover() {
		return this.turnover;
	}

	public void setTurnover(double turnover) {
		this.turnover = turnover;
	}

	public double getTurnoverVolume() {
		return this.turnoverVolume;
	}

	public void setTurnoverVolume(double turnoverVolume) {
		this.turnoverVolume = turnoverVolume;
	}

}
package com.sql.po;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the data_base database table.
 * 
 */
@Embeddable
public class DataBasePoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int id;

	@Temporal(TemporalType.DATE)
	private java.util.Date date;

	public DataBasePoPK() {
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public java.util.Date getDate() {
		return this.date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DataBasePoPK)) {
			return false;
		}
		DataBasePoPK castOther = (DataBasePoPK)other;
		return 
			(this.id == castOther.id)
			&& this.date.equals(castOther.date);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id;
		hash = hash * prime + this.date.hashCode();
		
		return hash;
	}
}
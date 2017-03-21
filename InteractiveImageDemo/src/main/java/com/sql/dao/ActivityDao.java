package com.sql.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sql.po.TActivityPo;



public interface ActivityDao extends CrudRepository<TActivityPo, Integer> {
	@Query(value = "select * from t_activity", nativeQuery = true)
	List<TActivityPo> getAll();
}

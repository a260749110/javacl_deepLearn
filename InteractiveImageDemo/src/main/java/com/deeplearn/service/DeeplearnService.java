package com.deeplearn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.dao.ActivityDao;
import com.sql.dao.DataBaseDao;
import com.sql.dao.DeeplearnResultDao;
import com.sql.dao.DeeplearnValueDao;
import com.sql.po.DataBasePo;
import com.sun.accessibility.internal.resources.accessibility;

@Service
public class DeeplearnService {
	@Autowired
	public DataBaseDao baseDao;
	@Autowired
	public ActivityDao activityDao;
	@Autowired
	public DeeplearnResultDao deeplearnResultDao;
	@Autowired
	public DeeplearnValueDao deeplearnValueDao;
	public List<DataBasePo> findAllDataBaseById(int id) {
		return baseDao.findALLById(id);
	}
}

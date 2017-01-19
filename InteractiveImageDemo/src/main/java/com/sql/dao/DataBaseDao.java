package com.sql.dao;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sql.po.DataBasePo;
import com.sql.po.DataBasePoPK;

public interface DataBaseDao extends CrudRepository<DataBasePo, DataBasePoPK>{
@Query(value = "select * from data_base where id=:id order by `date` asc", nativeQuery = true)
 List<DataBasePo> findALLById(@Param("id")int id);
}

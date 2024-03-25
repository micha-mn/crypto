package com.example.excelreaderdbwriter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.DataDTO;
import com.example.excelreaderdbwriter.enums.TableNameEnum;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Service
public class CryptoAnalyseService {
	
	@PersistenceContext
    private EntityManager entityManager;
	 
	 @Transactional
	    public void insertIntoTable(DataDTO dataDTO) {
	        String sequenceQuery = "select next_val from " + dataDTO.getTableName() + "_sequence";
            
	        Query sequenceNativeQuery = entityManager.createNativeQuery(sequenceQuery);
	        BigInteger nextId = (BigInteger) sequenceNativeQuery.getSingleResult();

	        Long id = nextId.longValue();

	        String insertQuery = "insert into " + dataDTO.getTableName() + " (id, refer_date, value) values (:id, :referDate, :value)";

	        Query nativeInsertQuery = entityManager.createNativeQuery(insertQuery);

	        nativeInsertQuery.setParameter("id", id);
	        nativeInsertQuery.setParameter("referDate", dataDTO.getReferDate());
	        nativeInsertQuery.setParameter("value", dataDTO.getValue());

	        int rowsAffected = nativeInsertQuery.executeUpdate();
	        
	        updateNextVal(dataDTO.getTableName());
	    }
	    
	  @Transactional
	    public void updateNextVal(String tableName) {
	        String nativeQuery = "UPDATE " + tableName + "_sequence SET next_val = next_val + 1 WHERE id = 1";
	        entityManager.createNativeQuery(nativeQuery).executeUpdate();
	    }
	 
	  public List<Map<String, String>> getTableNameEnum()
	  {
		  TableNameEnum[] values = TableNameEnum.values();
	        List<Map<String, String>> resultList = new ArrayList<>();

	        for (TableNameEnum value : values) {
	            Map<String, String> map = new HashMap<>();
	            map.put("tableName", value.getTableName());
	            map.put("description", value.getDescription());
	            resultList.add(map);
	        }
	        return resultList;
	  }

}

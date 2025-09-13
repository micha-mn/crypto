package com.crypto.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Ena;
@Repository
public interface EnaRepository extends JpaRepository<Ena, Long> {
	
	 
	  
}

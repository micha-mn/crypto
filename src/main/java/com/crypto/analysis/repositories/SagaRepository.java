package com.crypto.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Doge;
import com.crypto.analysis.domain.Ena;
import com.crypto.analysis.domain.Saga;
import com.crypto.analysis.domain.W;
@Repository
public interface SagaRepository extends JpaRepository<Saga, Long> {
	
	 
	  
}

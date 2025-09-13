package com.crypto.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Bnb;

@Repository
public interface BnbRepository extends JpaRepository<Bnb, Long> {
	
	 
	  
}

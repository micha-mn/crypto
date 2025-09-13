package com.crypto.analysis.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Btc;
@Repository
	
public interface BtcRepository extends JpaRepository<Btc, Long> {
	
	List<Btc> findTop2ByOrderByReferDateDesc(); 
}

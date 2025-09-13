package com.crypto.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Bnb;
import com.crypto.analysis.domain.Btc;
import com.crypto.analysis.domain.Doge;
import com.crypto.analysis.domain.Ena;
import com.crypto.analysis.domain.EnaTradeInfo;
import com.crypto.analysis.domain.Eth;
import com.crypto.analysis.domain.Saga;
import com.crypto.analysis.domain.W;
@Repository
public interface EnaTradeInfoRepository extends JpaRepository<EnaTradeInfo, Long> {
	
	 
	  
}

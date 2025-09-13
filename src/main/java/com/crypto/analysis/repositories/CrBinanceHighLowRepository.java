package com.crypto.analysis.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrBinanceHighLow;
import com.crypto.analysis.domain.CrXrpHighLow;

public interface CrBinanceHighLowRepository extends JpaRepository<CrBinanceHighLow, Long>  {

	 Optional<CrBinanceHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
}

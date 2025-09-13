package com.crypto.analysis.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crypto.analysis.domain.CrBTCHighLow;

public interface CrBTCHighLowRepository extends JpaRepository<CrBTCHighLow, Long>  {

	 Optional<CrBTCHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
	 
}

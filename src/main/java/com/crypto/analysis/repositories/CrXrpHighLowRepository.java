package com.crypto.analysis.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrXrpHighLow;

public interface CrXrpHighLowRepository extends JpaRepository<CrXrpHighLow, Long>  {
	 Optional<CrXrpHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
}

package com.crypto.analysis.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrSolanaHighLow;

public interface CrSolanaHighLowRepository extends JpaRepository<CrSolanaHighLow, Long>  {

	 Optional<CrSolanaHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
}

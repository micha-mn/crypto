package com.crypto.analysis.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrEthereumHighLow;

public interface CrEthereumHighLowRepository extends JpaRepository<CrEthereumHighLow, Long>  {

	 Optional<CrEthereumHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
}

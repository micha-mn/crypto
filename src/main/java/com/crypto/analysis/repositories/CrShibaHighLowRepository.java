package com.crypto.analysis.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrShibaHighLow;

public interface CrShibaHighLowRepository extends JpaRepository<CrShibaHighLow, Long>  {

	 Optional<CrShibaHighLow> findTopByOrderByStartTimeStampDesc();
	 boolean existsByStartTimeStamp(long startTimeStamp);
}

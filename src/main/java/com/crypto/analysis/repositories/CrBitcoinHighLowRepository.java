package com.crypto.analysis.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crypto.analysis.domain.CrBitcoinHighLow;

public interface CrBitcoinHighLowRepository extends JpaRepository<CrBitcoinHighLow, Long>  {


}

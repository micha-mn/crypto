package com.crypto.analysis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.analysis.domain.Bitcoin;

@Repository
public interface BitcoinRepository extends JpaRepository<Bitcoin, Long> {

}

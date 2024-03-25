package com.example.excelreaderdbwriter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.excelreaderdbwriter.domain.Bitcoin;

@Repository
public interface BitcoinRepository extends JpaRepository<Bitcoin, Long> {

}

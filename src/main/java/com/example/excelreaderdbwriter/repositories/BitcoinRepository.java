package com.example.excelreaderdbwriter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.excelreaderdbwriter.domain.Bitcoin;

public interface BitcoinRepository extends JpaRepository<Bitcoin, Long> {

}

package com.crypto.analysis.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Entity
@Table(name = "cr_bitcoin_high_low")
public class CrBitcoinHighLow {
	@Id
	 @GeneratedValue(generator = "cr_bitcoin_high_low_seq")
	 @GenericGenerator(
	      name = "cr_bitcoin_high_low_seq",
	      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	      parameters = {
	        @Parameter(name = "sequence_name", value = "cr_bitcoin_high_low_seq"),
	        @Parameter(name = "initial_value", value = "1"),
	        @Parameter(name = "increment_size", value = "1")
	        }
	    )
	private Long id;
	@Column(name = "openeur")
   private BigDecimal  openeur;
	@Column(name = "closeeur")
   private BigDecimal  closeeur;
   @Column(name = "high")
   private BigDecimal  high;
	@Column(name = "low")
   private BigDecimal  low;
	@Column(name = "volume")
   private BigDecimal  volume;
	@Column(name = "marketcap")
   private BigDecimal  marketcap;
   @Column(name = "openint")
   private BigDecimal  openint;
	@Column(name = "closeint")
   private BigDecimal  closeint;
	@Column(name = "start_time")
   private LocalDateTime startTime;
	@Column(name = "end_time")
   private LocalDateTime endTime;
}

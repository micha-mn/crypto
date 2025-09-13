package com.crypto.analysis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class CrCryptoDTO {
	    private Long id;
	    private BigDecimal high;
	    private BigDecimal low;
	    private BigDecimal volume;
	    private BigDecimal marketcap;
	    private BigDecimal totalVolume;
	    private BigDecimal open;
	    private BigDecimal close;
	    private LocalDateTime startTime;
	    private LocalDateTime endTime;
	    private Long startTimeStamp;
	    private Long endTimeStamp;
		private LocalDateTime referDate;
}

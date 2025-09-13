package com.crypto.analysis.websocket.BinanceKline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class BinanceKlineDTO {
    private String symbol;
    private long openTime;
    private long closeTime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private boolean isFinal;
}
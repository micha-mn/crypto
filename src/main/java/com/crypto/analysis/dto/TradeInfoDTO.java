package com.crypto.analysis.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class TradeInfoDTO {
	@JsonProperty("id")
	private String id;
	@JsonProperty("price")
    private String price;
	@JsonProperty("qty")
    private String qty;
	@JsonProperty("quoteQty")
    private String quoteQty;
	@JsonProperty("time")
    private String time;
	@JsonProperty("isBuyerMaker")
    private boolean isBuyerMaker;
	@JsonProperty("isBestMatch")
    private String isBestMatch;
}

package com.crypto.analysis.dto;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class OrderBookResponseDTO {
	
	private List<OrderBookByActionObjectProjection> bid;
	private List<OrderBookByActionObjectProjection> ask;

}

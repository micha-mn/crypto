package com.crypto.analysis.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class GraphFullResponseDTO {
	private GraphGeneralResponseDTO dataNormal;
	private GraphGeneralResponseDTO dataVolume;
	private GraphGeneralResponseDTO dataCandle;
	private GraphGeneralResponseDTO dataMax;
	private GraphGeneralResponseDTO dataMin;
	private GraphGeneralResponseDTO dataSellOrder;
	private GraphGeneralResponseDTO dataBuyOrder;
}

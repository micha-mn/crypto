package com.example.excelreaderdbwriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class ExportExcelDTO {
	 String fromPeriod;
	 String toPeriod;
}

package com.crypto.analysis.dto.prices;

import java.time.LocalDateTime;

import com.crypto.analysis.domain.Btc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class BtcDTO {
    private String value;
    private LocalDateTime referDate;
    
public BtcDTO(Btc btc) {
		
        this.value = btc.getValue();
        this.referDate = btc.getReferDate();
    }
}

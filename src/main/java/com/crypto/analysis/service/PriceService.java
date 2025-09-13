package com.crypto.analysis.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.crypto.analysis.domain.Btc;
import com.crypto.analysis.domain.CrBTCHighLow;
import com.crypto.analysis.domain.CrBinanceHighLow;
import com.crypto.analysis.domain.CrEthereumHighLow;
import com.crypto.analysis.domain.CrShibaHighLow;
import com.crypto.analysis.domain.CrSolanaHighLow;
import com.crypto.analysis.domain.CrXrpHighLow;
import com.crypto.analysis.dto.DataDTO;
import com.crypto.analysis.dto.GraphResponseDTO;
import com.crypto.analysis.dto.prices.BtcDTO;
import com.crypto.analysis.dto.prices.CurrencyPreviousPriceDTO;
import com.crypto.analysis.enums.CryptoSymbol;
import com.crypto.analysis.repositories.BtcRepository;
import com.crypto.analysis.repositories.CrBTCHighLowRepository;
import com.crypto.analysis.repositories.CrBinanceHighLowRepository;
import com.crypto.analysis.repositories.CrEthereumHighLowRepository;
import com.crypto.analysis.repositories.CrShibaHighLowRepository;
import com.crypto.analysis.repositories.CrSolanaHighLowRepository;
import com.crypto.analysis.repositories.CrXrpHighLowRepository;


@Service
public class PriceService {
	
	@Autowired
	private BtcRepository btcRepository;
	
	public  List<BtcDTO> getCurrentPreviousPrice() {
	
		List<Btc> resp = btcRepository.findTop2ByOrderByReferDateDesc();
		
		List<BtcDTO> dtos = resp.stream()
			    .map(BtcDTO::new)
			    .collect(Collectors.toList());
		return dtos;
		
	}
}

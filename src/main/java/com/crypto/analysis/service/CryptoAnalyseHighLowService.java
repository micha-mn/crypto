package com.crypto.analysis.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.crypto.analysis.domain.CrBTCHighLow;
import com.crypto.analysis.domain.CrBinanceHighLow;
import com.crypto.analysis.domain.CrEthereumHighLow;
import com.crypto.analysis.domain.CrShibaHighLow;
import com.crypto.analysis.domain.CrSolanaHighLow;
import com.crypto.analysis.domain.CrXrpHighLow;
import com.crypto.analysis.dto.CrCryptoDTO;
import com.crypto.analysis.dto.GraphResponseDTO;
import com.crypto.analysis.enums.CryptoSymbol;
import com.crypto.analysis.repositories.CrBTCHighLowRepository;
import com.crypto.analysis.repositories.CrBinanceHighLowRepository;
import com.crypto.analysis.repositories.CrEthereumHighLowRepository;
import com.crypto.analysis.repositories.CrShibaHighLowRepository;
import com.crypto.analysis.repositories.CrSolanaHighLowRepository;
import com.crypto.analysis.repositories.CrXrpHighLowRepository;
import com.crypto.analysis.websocket.BinanceKline.CombinedKlineWebSocketClient;
import com.crypto.analysis.websocket.BinanceKline.dto.BinanceKlineDTO;


@Service
public class CryptoAnalyseHighLowService {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private static final Logger log = LoggerFactory.getLogger(CryptoAnalyseHighLowService.class);

	private final RestTemplate restTemplate = new RestTemplate();
	private static final String BINANCE_KLINE_URL = "https://api.binance.com/api/v3/klines";
	private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets";

	@Autowired
	CrBTCHighLowRepository crBTCHighLowRepository;
	@Autowired
	CrBinanceHighLowRepository crBinanceHighLowRepository;
	@Autowired
	CrXrpHighLowRepository crXrpHighLowRepository;
	@Autowired
	CrShibaHighLowRepository crShibaHighLowRepository;
	@Autowired
	CrSolanaHighLowRepository crSolanaHighLowRepository;
	@Autowired
	CrEthereumHighLowRepository crEthereumHighLowRepository;
	@Autowired
	private CoinGeckoMarketDataService coinGeckoMarketDataService;

	@Autowired
	private EntityManager entityManager;

	// Cryptocurrencies to fetch data for
	private static final String[] BINANCE_SYMBOLS = { "BTCUSDT", "ETHUSDT", "SOLUSDT", "SHIBUSDT", "BNBUSDT",
			"XRPUSDT" };
	
	private static final String[] COINGECKO_SYMBOLS = { "bitcoin", "ethereum", "solana", "shiba-inu", "binancecoin",
			"ripple" };
	

	@PostConstruct
	public void startCombinedStream() {
	    List<String> symbols = Arrays.asList("BTCUSDT", "ETHUSDT", "SOLUSDT", "SHIBUSDT", "BNBUSDT", "XRPUSDT");
	    try {
	        CombinedKlineWebSocketClient client = new CombinedKlineWebSocketClient(symbols, this, coinGeckoMarketDataService);
	        client.connect();
	    } catch (Exception e) {
	        System.err.println("❌ Failed to start combined WebSocket client: " + e.getMessage());
	    }
	}
	public void saveCryptoSnapshot(String symbol, LocalDateTime startTime, LocalDateTime endTime,
            Map<String, Object> klineData,
            BigDecimal marketCap, BigDecimal totalVolume,
            BigDecimal fundingRate) {

		System.out.println("--- "+symbol+" --- "+startTime);
		
			long startTimestamp = Long.parseLong(klineData.getOrDefault("startTime", "0").toString());
			long endTimestamp = Long.parseLong(klineData.getOrDefault("endTime", "0").toString());
			
			BigDecimal open = new BigDecimal(klineData.getOrDefault("open", "0").toString());
			BigDecimal high = new BigDecimal(klineData.getOrDefault("high", "0").toString());
			BigDecimal low = new BigDecimal(klineData.getOrDefault("low", "0").toString());
			BigDecimal close = new BigDecimal(klineData.getOrDefault("close", "0").toString());
			BigDecimal volume = new BigDecimal(klineData.getOrDefault("volume", "0").toString());
			
			switch (symbol.toUpperCase()) {
			case "BTCUSDT":
			crBTCHighLowRepository.save(
			CrBTCHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			case "ETHUSDT":
			crEthereumHighLowRepository.save(
			CrEthereumHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			case "BNBUSDT":
			crBinanceHighLowRepository.save(
			CrBinanceHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			case "SOLUSDT":
			crSolanaHighLowRepository.save(
			CrSolanaHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			case "SHIBUSDT":
			crShibaHighLowRepository.save(
			CrShibaHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			case "XRPUSDT":
			crXrpHighLowRepository.save(
			CrXrpHighLow.builder()
			.open(open).high(high).low(low).close(close).volume(volume)
			.marketcap(marketCap).totalVolume(totalVolume)
			.fundingRate(fundingRate)
			.startTime(startTime).endTime(endTime)
			.startTimeStamp(startTimestamp).endTimeStamp(endTimestamp)
			.referDate(LocalDateTime.now())
			.build()
			);
			break;
			
			default:
			System.out.println("❌ Unrecognized symbol for saving: " + symbol);
			}
			
			System.out.println("✅ Saved snapshot for: " + symbol);
			}

	
	public void fetchCryptoData(LocalDateTime startTime, LocalDateTime endTime) {
		System.out.println("Fetching data at: " + LocalDateTime.now());

		Map<String, Map<String, Double>> marketCapAndVolumeData = fetchMarketCapAndVolumeData();

		for (int i = 0; i < BINANCE_SYMBOLS.length; i++) {
			String binanceSymbol = BINANCE_SYMBOLS[i]; // Binance symbol
			String coingeckoSymbol = COINGECKO_SYMBOLS[i]; // CoinGecko ID

			System.out.println("Fetching 3 minutes cacndle data for symbol: " + binanceSymbol);

			// Fetch OHLC data from Binance
			Map<String, Object> intlData = fetchKlineData(binanceSymbol, "1m", startTime, endTime);

			Map<String, Double> data = marketCapAndVolumeData.getOrDefault(coingeckoSymbol, new HashMap<>());
			Double marketCap = data.getOrDefault("market_cap", 0.0);
			Double totalVolume = data.getOrDefault("total_volume", 0.0);
			
			BigDecimal marketCapValue = BigDecimal.valueOf(marketCap);
			BigDecimal totalVolumeValue = BigDecimal.valueOf(totalVolume);
			
			if (binanceSymbol.equalsIgnoreCase("BTCUSDT") && !intlData.isEmpty()) {
				
				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("BTCUSDT");
				}
				CrBTCHighLow entity = CrBTCHighLow.builder()
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString()))
						.startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();

				// Save entity to the database
				crBTCHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			} else if (binanceSymbol.equalsIgnoreCase("BNBUSDT") && !intlData.isEmpty()) {
				
				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("BNBUSDT");
				}
				
				CrBinanceHighLow entity = CrBinanceHighLow.builder()
						// Save Euro-time OHLC data
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString())).startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();
				
				// Save entity to the database
				crBinanceHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			} else if (binanceSymbol.equalsIgnoreCase("ETHUSDT") && !intlData.isEmpty()) {
				
				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("ETHUSDT");
				}
				
				CrEthereumHighLow entity = CrEthereumHighLow.builder()
						// Save Euro-time OHLC data
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString())).startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();

				// Save entity to the database
				crEthereumHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			} else if (binanceSymbol.equalsIgnoreCase("SOLUSDT") && !intlData.isEmpty()) {

				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("SOLUSDT");
				}
				
				CrSolanaHighLow entity = CrSolanaHighLow.builder()
						// Save Euro-time OHLC data
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString())).startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();

				// Save entity to the database
				crSolanaHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			} else if (binanceSymbol.equalsIgnoreCase("SHIBUSDT") && !intlData.isEmpty()) {

				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("1000SHIBUSDT");
				}
				
				CrShibaHighLow entity = CrShibaHighLow.builder()
						// Save Euro-time OHLC data
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString())).startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();

				// Save entity to the database
				crShibaHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			} else if (binanceSymbol.equalsIgnoreCase("XRPUSDT") && !intlData.isEmpty()) {
				
				long startTimestamp = Long.parseLong(intlData.getOrDefault("startTime", "0").toString());
				BigDecimal fundingRate = null;
				if (isValidFundingInterval(startTimestamp)) {
				    fundingRate = fetchFundingRate("XRPUSDT");
				}
				
				CrXrpHighLow entity = CrXrpHighLow.builder()
						// Save Euro-time OHLC data
						.high(new BigDecimal(intlData.getOrDefault("high", "0").toString()))
						.low(new BigDecimal(intlData.getOrDefault("low", "0").toString()))
						.volume(new BigDecimal(intlData.getOrDefault("volume", "0").toString()))
						.marketcap(marketCapValue) 
						.totalVolume(totalVolumeValue)
						.open(new BigDecimal(intlData.getOrDefault("open", "0").toString()))
						.close(new BigDecimal(intlData.getOrDefault("close", "0").toString())).startTime(startTime)
						.endTime(endTime)
						.fundingRate(fundingRate)
						.startTimeStamp(Long.valueOf(intlData.getOrDefault("startTime", "0").toString()))
						.endTimeStamp(Long.valueOf(intlData.getOrDefault("endTime", "0").toString()))
						.referDate(LocalDateTime.now())
						.build();

				// Save entity to the database
				crXrpHighLowRepository.save(entity);
				System.out.println("Saved combined data for: " + binanceSymbol);
			}

			else {
				System.out.println("No data available for: " + binanceSymbol);
			}
		}
	}
	public boolean isValidFundingInterval(long startTimestamp) {
	    LocalDateTime dateTime = LocalDateTime.ofInstant(
	        java.time.Instant.ofEpochMilli(startTimestamp),
	        ZoneId.of("UTC")
	    );

	    int hour = dateTime.getHour();
	    int minute = dateTime.getMinute();

	    return (minute == 0) && (hour == 0 || hour == 8 || hour == 16);
	}

	public BigDecimal fetchFundingRate(String symbol) {
	    try {
	        String url = "https://fapi.binance.com/fapi/v1/fundingRate?symbol=" + symbol + "&limit=1";
	        String response = restTemplate.getForObject(url, String.class);
	        JSONArray jsonArray = new JSONArray(response);
	        if (jsonArray.length() > 0) {
	            JSONObject fundingData = jsonArray.getJSONObject(0);
	            return new BigDecimal(fundingData.getString("fundingRate"));
	        }
	    } catch (Exception e) {
	        log.error("Failed to fetch funding rate for " + symbol, e);
	    }
	    return BigDecimal.ZERO;
	}
	/**
	 * Fetch OHLC (Open, High, Low, Close, Volume) from Binance Kline API.
	 */
	private Map<String, Object> fetchKlineData(String symbol, String interval, LocalDateTime startTime,
			LocalDateTime endTime) {
		System.out.println("fetchKlineData before epocmili: startTime "+startTime);
		System.out.println("fetchKlineData:before epocmili endTime "+endTime);
		long startTimestamp = startTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
		long endTimestamp = endTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
		System.out.println("fetchKlineData epocmili: startTimestamp "+startTimestamp);
		System.out.println("fetchKlineData:epocmili endTimestamp "+endTimestamp);
		String url = UriComponentsBuilder.fromHttpUrl(BINANCE_KLINE_URL).queryParam("symbol", symbol)
				.queryParam("interval", interval).queryParam("startTime", startTimestamp)
				.queryParam("endTime", endTimestamp)
				.queryParam("limit", 1).toUriString();

		
		try {
			List<List<Object>> response = restTemplate.getForObject(url, List.class);
			Map<String, Object> data = new HashMap<>();

			if (response != null && !response.isEmpty()) {
				List<Object> firstCandle = response.get(0); // First candle
				List<Object> lastCandle = response.get(response.size() - 1); // Last candle
				
				data.put("startTime", firstCandle.get(0));
				data.put("open", firstCandle.get(1));
				data.put("close", lastCandle.get(4));
				data.put("high", lastCandle.get(2));
				data.put("low", lastCandle.get(3));
				data.put("volume", lastCandle.get(7));
				data.put("endTime", firstCandle.get(6));
			}
			return data;
		} catch (Exception e) {
			System.out.println("Error fetching OHLC data for " + symbol + ": " + e.getMessage());
			return Collections.emptyMap();
		}
	}
	
		/**
		 * Fetch market cap data from CoinGecko for multiple cryptocurrencies.
		 */
	public Map<String, Map<String, Double>> fetchMarketCapAndVolumeData() {
	    String symbols = String.join(",", COINGECKO_SYMBOLS);
	    String url = UriComponentsBuilder.fromHttpUrl(COINGECKO_API_URL)
	            .queryParam("vs_currency", "usd")
	            .queryParam("ids", symbols)
	            .toUriString();

	    try {
	        String response = restTemplate.getForObject(url, String.class);
	        JSONArray jsonArray = new JSONArray(response);
	        Map<String, Map<String, Double>> result = new HashMap<>();

	        for (int i = 0; i < jsonArray.length(); i++) {
	            JSONObject jsonObject = jsonArray.getJSONObject(i);
	            String id = jsonObject.getString("id");
	            double marketCap = jsonObject.getDouble("market_cap");
	            double totalVolume = jsonObject.getDouble("total_volume");
	            Map<String, Double> data = new HashMap<>();
	            data.put("market_cap", marketCap);
	            data.put("total_volume", totalVolume);
	            result.put(id, data);
	        }
	        System.out.println("MarketCap and Volume Data -- " + result);
	        return result;
	    } catch (Exception e) {
	        log.error("Error fetching market cap and volume data from CoinGecko: " + e.getMessage());
	        return Collections.emptyMap();
	    }
	}

		public void runDailyCryptoTask(String fromDate, String toDate) {
			
			for (int i = 0; i < BINANCE_SYMBOLS.length; i++) {
			
		        CryptoSymbol symbol = CryptoSymbol.fromString(BINANCE_SYMBOLS[i]);
		    	String tableName = symbol.getTableName(); 
		    	String groupId = symbol.getGroupId(); 
		    	
		   		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_dynamic_calculation_daily_data");
		   		query.registerStoredProcedureParameter("fromDate", String.class, ParameterMode.IN);
				query.setParameter("fromDate", fromDate);
				query.registerStoredProcedureParameter("toDateDate", String.class, ParameterMode.IN);
				query.setParameter("toDateDate", toDate);
				query.registerStoredProcedureParameter("tableName", String.class, ParameterMode.IN);
				query.setParameter("tableName", tableName);
				query.registerStoredProcedureParameter("groupId", String.class, ParameterMode.IN);
				query.setParameter("groupId", groupId);
				query.execute();
				
				 entityManager.clear();  // Keep clear() to free resources, but remove close()
			}
		}
		
		 public Optional<CrBTCHighLow> getLatestBtc() {
			return  crBTCHighLowRepository.findTopByOrderByStartTimeStampDesc();
	    }
		 
		 public List<CrCryptoDTO> getLatestBtcList() {
			 
		     Optional<CrBTCHighLow> crHighLowList=  crBTCHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
		     
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMap("1d","BTCUSDT",totalVolume,marketcap));
		     results.add(fetchAndMap("4h","BTCUSDT",totalVolume,marketcap));
		     return results;
		 }

		 public List<CrCryptoDTO> getLatestEthList() {
			 
			 Optional<CrEthereumHighLow> crHighLowList=  crEthereumHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
			 
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMap("1d","ETHUSDT",totalVolume,marketcap));
		     results.add(fetchAndMap("4h","ETHUSDT",totalVolume,marketcap));
		     return results;
		 }

		 public List<CrCryptoDTO> getLatestSolList() {
			 
			 Optional<CrSolanaHighLow> crHighLowList=  crSolanaHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
			 
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMap("1d","SOLUSDT",totalVolume,marketcap));
		     results.add(fetchAndMap("4h","SOLUSDT",totalVolume,marketcap));
		     return results;
		 }
		 
		 public List<CrCryptoDTO> getLatestShibaList() {
			 
			 Optional<CrShibaHighLow> crHighLowList=  crShibaHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
			 
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMapShiba("1d","SHIBUSDT",totalVolume,marketcap));
		     results.add(fetchAndMapShiba("4h","SHIBUSDT",totalVolume,marketcap));
		     return results;
		 }
		 
		 public List<CrCryptoDTO> getLatestBnbList() {
			 
			 Optional<CrBinanceHighLow> crHighLowList=  crBinanceHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
			 
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMap("1d","BNBUSDT",totalVolume,marketcap));
		     results.add(fetchAndMap("4h","BNBUSDT",totalVolume,marketcap));
		     return results;
		 }
		 public List<CrCryptoDTO> getLatestXrpList() {
			 
			 Optional<CrXrpHighLow> crHighLowList=  crXrpHighLowRepository.findTopByOrderByStartTimeStampDesc();
		     BigDecimal totalVolume =  crHighLowList.get().getTotalVolume();
		     BigDecimal marketcap =  crHighLowList.get().getMarketcap();
			 
		     List<CrCryptoDTO> results = new ArrayList<>();
		     results.add(fetchAndMap("1d","XRPUSDT",totalVolume,marketcap));
		     results.add(fetchAndMap("4h","XRPUSDT",totalVolume,marketcap));
		     return results;
		 }
		 
		 private CrCryptoDTO fetchAndMap(String interval, String symbol, BigDecimal totalVolume, BigDecimal marketcap) {
		     String url = String.format("https://api.binance.com/api/v3/klines?symbol="+symbol+"&interval=%s&limit=1", interval);

		     ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, null, List.class);
		     List<Object> klineData = (List<Object>) response.getBody().get(0);
		     return CrCryptoDTO.builder()
		         .open(new BigDecimal((String) klineData.get(1)))
		         .high(new BigDecimal((String) klineData.get(2)))
		         .low(new BigDecimal((String) klineData.get(3)))
		         .close(new BigDecimal((String) klineData.get(4)))
		         .volume(new BigDecimal((String) klineData.get(7)))
		         .totalVolume(totalVolume)
		         .marketcap(marketcap)
		         .startTime(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) klineData.get(0)), ZoneId.systemDefault()))
		         .endTime(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) klineData.get(6)), ZoneId.systemDefault()))
		         .startTimeStamp((Long) klineData.get(0))
		         .endTimeStamp((Long) klineData.get(6))
		         .referDate(LocalDateTime.now())
		         .build();
		 }
		 private CrCryptoDTO fetchAndMapShiba(String interval, String symbol, BigDecimal totalVolume, BigDecimal marketcap) {
		     String url = String.format("https://api.binance.com/api/v3/klines?symbol="+symbol+"&interval=%s&limit=1", interval);

		     ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, null, List.class);
		     List<Object> klineData = (List<Object>) response.getBody().get(0);
		     return CrCryptoDTO.builder()
		         .open(new BigDecimal((String) klineData.get(1)).multiply(BigDecimal.valueOf(1000)))
		         .high(new BigDecimal((String) klineData.get(2)).multiply(BigDecimal.valueOf(1000)))
		         .low(new BigDecimal((String) klineData.get(3)).multiply(BigDecimal.valueOf(1000)))
		         .close(new BigDecimal((String) klineData.get(4)).multiply(BigDecimal.valueOf(1000)))
		         .volume(new BigDecimal((String) klineData.get(7)))
		         .totalVolume(totalVolume)
		         .marketcap(marketcap)
		         .startTime(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) klineData.get(0)), ZoneId.systemDefault()))
		         .endTime(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) klineData.get(6)), ZoneId.systemDefault()))
		         .startTimeStamp((Long) klineData.get(0))
		         .endTimeStamp((Long) klineData.get(6))
		         .referDate(LocalDateTime.now())
		         .build();
		 }
		public Optional<CrEthereumHighLow> getLatestEthereum() {
			return  crEthereumHighLowRepository.findTopByOrderByStartTimeStampDesc();
		 }
		public Optional<CrSolanaHighLow> getLatestSolana() {
			return  crSolanaHighLowRepository.findTopByOrderByStartTimeStampDesc();
		 }
		public Optional<CrShibaHighLow> getLatestShiba() {
			return crShibaHighLowRepository.findTopByOrderByStartTimeStampDesc();
		 }
		public Optional<CrXrpHighLow> getLatestXrp() {
			return  crXrpHighLowRepository.findTopByOrderByStartTimeStampDesc();
		 }
		public Optional<CrBinanceHighLow> getLatestBinance() {
			return  crBinanceHighLowRepository.findTopByOrderByStartTimeStampDesc();
		 }
		
		 public List<GraphResponseDTO> getfundingRate(String Symbol, String fromDate, String toDate) {
			 String tableName = CryptoSymbol.fromGroupId(Symbol).getTableName();
			 List<Object[]> rawResults = getFundingRate(tableName, fromDate, toDate);

			  return rawResults.stream()
				        .map(obj -> GraphResponseDTO.builder()
				            .id(((Number) obj[0]).longValue())
				            .x((String) obj[1])
				            .y(obj[2] != null ? obj[2].toString() : null)
				            .build())
				        .collect(Collectors.toList());
		 }
		 

public List<Object[]> getFundingRate(String tableName, String fromDate, String toDate) {
    String sql = "SELECT " +
            "  ROW_NUMBER() OVER (ORDER BY start_timestamp) AS id, " +
            "  DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(start_timestamp / 1000), @@session.time_zone, '+00:00'), '%d-%b-%y %H:%i') AS x, " +
            "  funding_rate AS y " +
            "FROM " + tableName + " " +
            "WHERE funding_rate IS NOT NULL " +
            "AND DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(start_timestamp / 1000), @@session.time_zone, '+00:00'), '%Y-%m-%d %H:%i:%s') " +
            "BETWEEN :fromDate AND :toDate";

    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);

    return query.getResultList();
}
public String importKlines(String symbol, long startTime, long endTime) {
    final long ONE_MINUTE_MS = 60 * 1000;
    final int MAX_LIMIT = 1000;
    long currentStart = startTime;

    try {
        while (currentStart < endTime) {
            long currentEnd = currentStart + (MAX_LIMIT * ONE_MINUTE_MS);
            if (currentEnd > endTime) {
                currentEnd = endTime;
            }

            String url = String.format(
                "https://api.binance.com/api/v3/klines?symbol=%s&interval=1m&startTime=%d&endTime=%d&limit=%d",
                symbol.toUpperCase(), currentStart, currentEnd, MAX_LIMIT
            );

            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            List<List<Object>> klines = response.getBody();

            if (klines == null || klines.isEmpty()) {
                break;
            }

            for (List<Object> k : klines) {
                long openTime = ((Number) k.get(0)).longValue();

                // Skip if data already exists
                if (existsByStartTimestamp(symbol, openTime)) {
                    continue;
                }

                BinanceKlineDTO dto = BinanceKlineDTO.builder()
                        .symbol(symbol.toUpperCase())
                        .openTime(openTime)
                        .closeTime(((Number) k.get(6)).longValue())
                        .open(new BigDecimal((String) k.get(1)))
                        .high(new BigDecimal((String) k.get(2)))
                        .low(new BigDecimal((String) k.get(3)))
                        .close(new BigDecimal((String) k.get(4)))
                        .volume(new BigDecimal((String) k.get(7)))
                        .isFinal(true)
                        .build();

                saveKlineWithoutMarketData(dto);
            }

            // Move to next batch
            currentStart = ((Number) klines.get(klines.size() - 1).get(0)).longValue() + ONE_MINUTE_MS;
        }

        return "✅ All klines imported successfully";

    } catch (Exception e) {
        return "❌ Error importing klines: " + e.getMessage();
    }
}
public boolean existsByStartTimestamp(String symbol, long timestamp) {
    switch (symbol.toUpperCase()) {
        case "BTCUSDT": return crBTCHighLowRepository.existsByStartTimeStamp(timestamp);
        case "ETHUSDT": return crEthereumHighLowRepository.existsByStartTimeStamp(timestamp);
        case "BNBUSDT": return crBinanceHighLowRepository.existsByStartTimeStamp(timestamp);
        case "SOLUSDT": return crSolanaHighLowRepository.existsByStartTimeStamp(timestamp);
        case "SHIBUSDT": return crShibaHighLowRepository.existsByStartTimeStamp(timestamp);
        case "XRPUSDT": return crXrpHighLowRepository.existsByStartTimeStamp(timestamp);
        default: return false;
    }
}
private void saveKlineWithoutMarketData(BinanceKlineDTO dto) {
    try {
        LocalDateTime startTime = Instant.ofEpochMilli(dto.getOpenTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTime = Instant.ofEpochMilli(dto.getCloseTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();

        Map<String, Object> klineMap = new HashMap<>();
        klineMap.put("startTime", dto.getOpenTime());
        klineMap.put("endTime", dto.getCloseTime());
        klineMap.put("open", dto.getOpen());
        klineMap.put("high", dto.getHigh());
        klineMap.put("low", dto.getLow());
        klineMap.put("close", dto.getClose());
        klineMap.put("volume", dto.getVolume());

        saveCryptoSnapshot(
                dto.getSymbol(),
                startTime,
                endTime,
                klineMap,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null
        );

    } catch (Exception e) {
        System.err.println("❌ Failed to save historical kline: " + e.getMessage());
    }
}

}

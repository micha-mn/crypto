package com.crypto.analysis.service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.crypto.analysis.domain.BTCTradeHistoryInfo;
import com.crypto.analysis.domain.BTCTradeInfo;
import com.crypto.analysis.domain.Bnb;
import com.crypto.analysis.domain.Btc;
import com.crypto.analysis.domain.Doge;
import com.crypto.analysis.domain.Ena;
import com.crypto.analysis.domain.EnaInfo;
import com.crypto.analysis.domain.EnaTrackingTable;
import com.crypto.analysis.domain.EnaTradeHistoryInfo;
import com.crypto.analysis.domain.EnaTradeInfo;
import com.crypto.analysis.domain.Eth;
import com.crypto.analysis.domain.EthFITrackingTable;
import com.crypto.analysis.domain.EthFITradeHistoryInfo;
import com.crypto.analysis.domain.EthFi;
import com.crypto.analysis.domain.Floki;
import com.crypto.analysis.domain.Pepe;
import com.crypto.analysis.domain.Saga;
import com.crypto.analysis.domain.W;
import com.crypto.analysis.domain.WInfo;
import com.crypto.analysis.domain.WTradeInfo;
import com.crypto.analysis.dto.CurrencyDTO;
import com.crypto.analysis.dto.CurrencyInfoDTO;
import com.crypto.analysis.dto.DataDTO;
import com.crypto.analysis.dto.GraphDataReqDTO;
import com.crypto.analysis.dto.GraphFulllResponseDTO;
import com.crypto.analysis.dto.GraphGeneralResponseDTO;
import com.crypto.analysis.dto.GraphResponseDTO;
import com.crypto.analysis.dto.GraphResponseProjection;
import com.crypto.analysis.dto.OrderBookResponseDTO;
import com.crypto.analysis.dto.PriceCryptoRespDTO;
import com.crypto.analysis.dto.Resistant;
import com.crypto.analysis.dto.SupResDTO;
import com.crypto.analysis.dto.Support;
import com.crypto.analysis.dto.SupportResistantPointsDTO;
import com.crypto.analysis.dto.TradeHistoryResDTO;
import com.crypto.analysis.dto.TradeInfoDTO;
import com.crypto.analysis.dto.TradeReqDTO;
import com.crypto.analysis.dto.TradeResponseDTO;
import com.crypto.analysis.enums.TableNameEnum;
import com.crypto.analysis.repositories.BTCTradeHistoryInfoRepository;
import com.crypto.analysis.repositories.BTCTradeInfoRepository;
import com.crypto.analysis.repositories.BnbRepository;
import com.crypto.analysis.repositories.BtcRepository;
import com.crypto.analysis.repositories.DogeRepository;
import com.crypto.analysis.repositories.EnaInfoRepository;
import com.crypto.analysis.repositories.EnaRepository;
import com.crypto.analysis.repositories.EnaTrackingRepository;
import com.crypto.analysis.repositories.EnaTradeHistoryInfoRepository;
import com.crypto.analysis.repositories.EnaTradeInfoRepository;
import com.crypto.analysis.repositories.EthFITrackingRepository;
import com.crypto.analysis.repositories.EthFITradeHistoryInfoRepository;
import com.crypto.analysis.repositories.EthFiRepository;
import com.crypto.analysis.repositories.EthRepository;
import com.crypto.analysis.repositories.FlokiRepository;
import com.crypto.analysis.repositories.PepeRepository;
import com.crypto.analysis.repositories.SagaRepository;
import com.crypto.analysis.repositories.WInfoRepository;
import com.crypto.analysis.repositories.WRepository;
import com.crypto.analysis.repositories.WTradeInfoRepository;
import com.crypto.analysis.util.CurrencyTableMapper;

@Service
public class CryptoAnalyseService {
	
	@PersistenceContext
    private EntityManager entityManager;
	private EthFiRepository ethFiRepository;
	private EnaRepository enaRepository;
	private WRepository wRepository;
	private DogeRepository dogeRepository;
	private SagaRepository sagaRepository;
	private BtcRepository btcRepository;
	private BnbRepository bnbRepository;
	private EthRepository ethRepository;
	private PepeRepository pepeRepository;
	private FlokiRepository flokiRepository;
	private EnaInfoRepository enaInfoRepository;
	private WInfoRepository wInfoRepository;
	private EnaTradeInfoRepository enaTradeInfoRepository;
	private WTradeInfoRepository  wTradeInfoRepository;
	private EnaTrackingRepository enaTrackingRepository;
	private EnaTradeHistoryInfoRepository enaTradeHistoryInfoRepository;
	private EthFITrackingRepository ethFITrackingRepository;
	private EthFITradeHistoryInfoRepository ethFITradeHistoryInfoRepository;
	private OrderBookService orderBookService;
	private BTCTradeInfoRepository btcTradeInfoRepository;
	private BTCTradeHistoryInfoRepository btcTradeHistoryInfoRepository;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public CryptoAnalyseService(EthFiRepository ethFiRepository,
			                    EnaRepository enaRepository,
			                    WRepository wRepository,
			                    DogeRepository dogeRepository,
			                    SagaRepository sagaRepository,
			                    BtcRepository btcRepository,
			                    BnbRepository bnbRepository,
			                    EthRepository ethRepository,
			                    PepeRepository pepeRepository,
			                    FlokiRepository flokiRepository,
			                    EnaInfoRepository enaInfoRepository,
			                    WInfoRepository wInfoRepository,
			                    EnaTradeInfoRepository enaTradeInfoRepository,
			                    WTradeInfoRepository  wTradeInfoRepository,
			                    EnaTrackingRepository enaTrackingRepository,
			                    EnaTradeHistoryInfoRepository enaTradeHistoryInfoRepository,
			                    EthFITrackingRepository ethFITrackingRepository,
			                    EthFITradeHistoryInfoRepository ethFITradeHistoryInfoRepository,
			                    OrderBookService orderBookService,
			                    BTCTradeInfoRepository btcTradeInfoRepository,
			                    BTCTradeHistoryInfoRepository btcTradeHistoryInfoRepository) {
		
		this.ethFiRepository                 = ethFiRepository;
		this.enaRepository                   = enaRepository;
		this.wRepository                     = wRepository;
		this.dogeRepository                  = dogeRepository;
		this.sagaRepository                  = sagaRepository;
		this.btcRepository                   = btcRepository;
		this.bnbRepository                   = bnbRepository;
		this.ethRepository                   = ethRepository;
		this.pepeRepository                  = pepeRepository;
		this.flokiRepository                 = flokiRepository;
		this.enaInfoRepository               = enaInfoRepository;
		this.wInfoRepository                 = wInfoRepository;
		this.enaTradeInfoRepository          = enaTradeInfoRepository;
		this.wTradeInfoRepository            = wTradeInfoRepository;
		this.enaTrackingRepository           = enaTrackingRepository;
		this.enaTradeHistoryInfoRepository   = enaTradeHistoryInfoRepository;
		this.ethFITrackingRepository         = ethFITrackingRepository;
		this.ethFITradeHistoryInfoRepository = ethFITradeHistoryInfoRepository;
		this.orderBookService                =  orderBookService;
		this.btcTradeInfoRepository          = btcTradeInfoRepository;
		this.btcTradeHistoryInfoRepository   = btcTradeHistoryInfoRepository;
	}
	
    
	/*
	 @Transactional
	    public boolean insertIntoTable(DataDTO dataDTO) {
	        String sequenceQuery = "select next_val from cr_" + dataDTO.getTableName() + "_sequence";
            
	        Query sequenceNativeQuery = entityManager.createNativeQuery(sequenceQuery);
	        BigInteger nextId = (BigInteger) sequenceNativeQuery.getSingleResult();

	        Long id = nextId.longValue();

	        String insertQuery = "insert into cr_" + dataDTO.getTableName() + " (id, refer_date, value) values (:id, :referDate, :value)";

	        Query nativeInsertQuery = entityManager.createNativeQuery(insertQuery);

	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy h:mm:ss a", Locale.ENGLISH);

	        //convert String to LocalDate
	        LocalDateTime localDate = LocalDateTime.parse(dataDTO.getReferDate(), formatter);
	        System.out.println(localDate);
	        
	        nativeInsertQuery.setParameter("id", id);
	        nativeInsertQuery.setParameter("referDate", localDate);
	        nativeInsertQuery.setParameter("value", dataDTO.getValue());

	        int rowsAffected = nativeInsertQuery.executeUpdate();
	        
	        updateNextVal(dataDTO.getTableName());
	        return true;
	    }
	    */
	    
	  @Transactional
	    public void updateNextVal(String tableName) {
	        String nativeQuery = "UPDATE cr_" + tableName + "_sequence SET next_val = next_val + 1";
	        entityManager.createNativeQuery(nativeQuery).executeUpdate();
	    }
	 
	  public List<Map<String, String>> getTableNameEnum()
	  {
		  TableNameEnum[] values = TableNameEnum.values();
	        List<Map<String, String>> resultList = new ArrayList<>();

	        for (TableNameEnum value : values) {
	            Map<String, String> map = new HashMap<>();
	            map.put("tableName", value.getTableName());
	            map.put("description", value.getDescription());
	            resultList.add(map);
	        }
	        return resultList;
	  }

	public List<DataDTO> getData(String tableName) {
	
		 String dataQuery = "select * from cr_" + tableName + " order by refer_date desc";
		    Query dataNativeQuery = entityManager.createNativeQuery(dataQuery);

		    List<Object[]> resultList = dataNativeQuery.getResultList();
		    List<DataDTO> dataDTOList = new ArrayList<>();

		    for (Object[] row : resultList) {
		        long id = ((Number) row[0]).longValue(); // Assuming the ID is a long
		        String value = (String) row[2];

		        DataDTO dataDTO =  DataDTO.builder().id(id)
		        									.referDate(String.valueOf( row[1]))
		        									.value(value)
		        									.tableName(tableName)
		        									.build();
		        dataDTOList.add(dataDTO);
		    }

		    return dataDTOList;
	}
	@Transactional
	public boolean updateData(DataDTO dataDTO) {
		
        String updateQuery = "update cr_" + dataDTO.getTableName() + " set  `value` =  :value WHERE `id` = :id";

        Query nativeUpdateQuery = entityManager.createNativeQuery(updateQuery);

        nativeUpdateQuery.setParameter("id", dataDTO.getId());
        nativeUpdateQuery.setParameter("value", dataDTO.getValue());

        int rowsAffected = nativeUpdateQuery.executeUpdate();
		return true;
	}
	@Transactional
	public boolean deleteData(String tablename, String id) {
		  String nativeQuery = "DELETE FROM cr_" + tablename + " where id= "+id;
	       entityManager.createNativeQuery(nativeQuery).executeUpdate();
	       return true;
	}
	
	
	
	public boolean saveLivePrices(PriceCryptoRespDTO[] dataLst) {
		   saveLivePricesAndCaclulateMinMAx(dataLst);
	       return true;
	}
	
	
	public void saveLivePricesAndCaclulateMinMAx(PriceCryptoRespDTO[] dataLst) {
		PriceCryptoRespDTO data = PriceCryptoRespDTO.builder().build();
		int checkTrackingCnt = 0;
		for(int i =0;i<dataLst.length; i++) {
			data = dataLst[i];
			if(data.getSymbol().equalsIgnoreCase("ETHFIUSDT")) {
				LocalDateTime ethFIInsertTime = LocalDateTime.now();
				Optional<EthFITrackingTable> ethFITrackingTableOpt ;
				EthFITrackingTable ethFITracking = EthFITrackingTable.builder().build();
				EthFi ethFi = EthFi.builder().referDate(LocalDateTime.now())
						.value(data.getPrice())
						.build();
				ethFiRepository.save(ethFi);
				ethFITrackingTableOpt = ethFITrackingRepository.findById(Long.valueOf("1"));
				ethFITracking = ethFITrackingTableOpt.get();
				checkTrackingCnt = Integer.parseInt(ethFITracking.getNotExecutedMinMaxPrice());
				if(checkTrackingCnt > 10) {
					// call procedure
					StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_calculate_max_min_graph");
			   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
			   		query.setParameter("cryptoCurrency","ETHFI" );
			   		query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
			   		query.setParameter("fromDate",ethFITracking.getLastDateMinMaxExecuted() );
			   		query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
			   		query.setParameter("toDate",ethFIInsertTime );
			   		query.registerStoredProcedureParameter("period", String.class, ParameterMode.IN);
			   		query.setParameter("period", "10" );
			   		query.execute();
				}else {
					ethFITracking.setNotExecutedMinMaxPrice(
							String.valueOf(Integer.parseInt(ethFITracking.getNotExecutedMinMaxPrice())+1)
							);
					// enaTracking.setLastDateMinMaxExecuted(LocalDateTime.now());
					ethFITrackingRepository.save(ethFITracking);
				}
			}else
			if(data.getSymbol().equalsIgnoreCase("ENAUSDT")) {
				LocalDateTime enaInsertTime = LocalDateTime.now();
				Optional<EnaTrackingTable> enaTrackingOpt ;
				EnaTrackingTable enaTracking = EnaTrackingTable.builder().build();
				enaInsertTime = LocalDateTime.now();
				Ena ena = Ena.builder().referDate(enaInsertTime)
						.value(data.getPrice())
						.build();
				enaRepository.save(ena);
				enaTrackingOpt = enaTrackingRepository.findById(Long.valueOf("1"));
				enaTracking = enaTrackingOpt.get();
				checkTrackingCnt = Integer.parseInt(enaTracking.getNotExecutedMinMaxPrice());
				if(checkTrackingCnt > 10) {
					// call procedure
					StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_calculate_max_min_graph");
			   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
			   		query.setParameter("cryptoCurrency","ENNA" );
			   		query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
			   		query.setParameter("fromDate",enaTracking.getLastDateMinMaxExecuted() );
			   		query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
			   		query.setParameter("toDate",enaInsertTime );
			   		query.registerStoredProcedureParameter("period", String.class, ParameterMode.IN);
			   		query.setParameter("period", "10" );
			   		query.execute();
				}else {
					enaTracking.setNotExecutedMinMaxPrice(
							String.valueOf(Integer.parseInt(enaTracking.getNotExecutedMinMaxPrice())+1)
							);
					// enaTracking.setLastDateMinMaxExecuted(LocalDateTime.now());
					enaTrackingRepository.save(enaTracking);
				}
				
			}else
			if(data.getSymbol().equalsIgnoreCase("WUSDT")) {
				W w = W.builder().referDate(LocalDateTime.now())
						.value(data.getPrice())
						.build();
				wRepository.save(w);
			}else
			if(data.getSymbol().equalsIgnoreCase("DOGEUSDT")) {
				Doge doge = Doge.builder().referDate(LocalDateTime.now())
						.value(data.getPrice())
						.build();
				dogeRepository.save(doge);
			}else
				if(data.getSymbol().equalsIgnoreCase("SAGAUSDT")) {
					Saga saga = Saga.builder().referDate(LocalDateTime.now())
							.value(data.getPrice())
							.build();
					sagaRepository.save(saga);
				}else
					if(data.getSymbol().equalsIgnoreCase("BTCUSDT")) {
						Btc btc = Btc.builder().referDate(LocalDateTime.now())
								.value(data.getPrice())
								.build();
						btcRepository.save(btc);
					}
					else
						if(data.getSymbol().equalsIgnoreCase("BNBUSDT")) {
							Bnb bnb = Bnb.builder().referDate(LocalDateTime.now())
									.value(data.getPrice())
									.build();
							bnbRepository.save(bnb);
						}else
							if(data.getSymbol().equalsIgnoreCase("ETHUSDT")) {
								Eth eth = Eth.builder().referDate(LocalDateTime.now())
										.value(data.getPrice())
										.build();
								ethRepository.save(eth);
							}else
								if(data.getSymbol().equalsIgnoreCase("PEPEUSDT")) {
									Pepe pepe = Pepe.builder().referDate(LocalDateTime.now())
											.value(data.getPrice())
											.build();
									pepeRepository.save(pepe);
								}else
									if(data.getSymbol().equalsIgnoreCase("FLOKIUSDT")) {
										Floki floki = Floki.builder().referDate(LocalDateTime.now())
												.value(data.getPrice())
												.build();
										flokiRepository.save(floki);
									}
		}
	}
	
	public void saveCurrencyInfo(CurrencyInfoDTO[] dataLst) {
		EnaInfo enaInfo = EnaInfo.builder().build();
		WInfo   wInfo = WInfo.builder().build();
		for(int i=0;i<dataLst.length;i++) {
			
		if(dataLst[i].getId().equalsIgnoreCase("ethena")) {
			enaInfo = buildEnaInfoEntity(dataLst[i]);
			enaInfoRepository.save(enaInfo);
		}else
			if(dataLst[i].getId().equalsIgnoreCase("wormhole")) {
			   wInfo = buildWInfoEntity(dataLst[i]);
			   wInfoRepository.save(wInfo);
		}
		
		
		}
	}
	
	
	public EnaInfo buildEnaInfoEntity(CurrencyInfoDTO data) {
		EnaInfo enaInfo = EnaInfo.builder().build();
		enaInfo = EnaInfo.builder()
				.id(data.getId())
				.circulatingSupply(data.getCirculatingSupply())
				.fullyDilutedMarketCap(data.getFullyDilutedMarketCap())
				.high24h(data.getHigh24h())
				.low24h(data.getLow24h())
				.marketCap(data.getMarketCap())
				.marketCapChange24h(data.getMarketCapChange24h())
				.marketCapChangePercentage24h(data.getMarketCapChangePercentage24h())
				.name(data.getName())
				.priceChange24h(data.getPriceChange24h())
				.priceChangePercentage24h(data.getPriceChangePercentage24h())
				.referDate(data.getReferDate())
				.symbol(data.getSymbol())
				.totalVolume(data.getTotalVolume())
				.build();
		return enaInfo;
	}
	
	public WInfo buildWInfoEntity(CurrencyInfoDTO data) {
		WInfo wInfo = WInfo.builder().build();
		wInfo = WInfo.builder()
				.id(data.getId())
				.circulatingSupply(data.getCirculatingSupply())
				.fullyDilutedMarketCap(data.getFullyDilutedMarketCap())
				.high24h(data.getHigh24h())
				.low24h(data.getLow24h())
				.marketCap(data.getMarketCap())
				.marketCapChange24h(data.getMarketCapChange24h())
				.marketCapChangePercentage24h(data.getMarketCapChangePercentage24h())
				.name(data.getName())
				.priceChange24h(data.getPriceChange24h())
				.priceChangePercentage24h(data.getPriceChangePercentage24h())
				.referDate(data.getReferDate())
				.symbol(data.getSymbol())
				.totalVolume(data.getTotalVolume())
				.build();
		return wInfo;
		
	}
	
	public void saveTradeInfo(TradeInfoDTO[] dataLst,String currrency) {
		
		
		if(currrency.equalsIgnoreCase("ENA")) {
			EnaTradeInfo enaTradeInfo = EnaTradeInfo.builder().build();
			List<EnaTradeInfo> enaTradeInfoLst = new ArrayList<EnaTradeInfo>();
			for(int i=0;i<dataLst.length;i++) {
				enaTradeInfo = buildEnaTradeInfoEntity(dataLst[i]);
				enaTradeInfoLst.add(enaTradeInfo);
			}
			enaTradeInfoRepository.saveAll(enaTradeInfoLst);
		}
	     else
			if(currrency.equalsIgnoreCase("W")) {
				WTradeInfo   wTradeInfo = WTradeInfo.builder().build();
				List<WTradeInfo> wTradeInfoLst = new ArrayList<WTradeInfo>();
				for(int i=0;i<dataLst.length;i++) {
					wTradeInfo = buildWTradeInfoEntity(dataLst[i]);
					wTradeInfoLst.add(wTradeInfo);
				}
				wTradeInfoRepository.saveAll(wTradeInfoLst);
		} 
	}
	
  public String saveHistoryTradeInfo(TradeInfoDTO[] dataLst,String currrency) {
    
	  List<TradeInfoDTO> tradeList = Arrays.asList(dataLst);
	    String lastHistoricalId = getMaxIdTrade(tradeList);
		if(currrency.equalsIgnoreCase("ENNA")) {
			EnaTradeHistoryInfo enaTradeHistoryInfo = EnaTradeHistoryInfo.builder().build();
			List<EnaTradeHistoryInfo> enaTradeHistoryInfoLst = new ArrayList<EnaTradeHistoryInfo>();
			long test_timestamp ;
			LocalDateTime triggerTime ;
			        
			for(int i=0;i<dataLst.length;i++) {
				test_timestamp = Long.valueOf(dataLst[i].getTime());
				triggerTime =
				        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
				                                TimeZone.getDefault().toZoneId()); 
				enaTradeHistoryInfo = enaTradeHistoryInfo.builder()
						.id(dataLst[i].getId())
						.isBestMatch(dataLst[i].getIsBestMatch())
						.isBuyerMaker(dataLst[i].isBuyerMaker())
						.price(dataLst[i].getPrice())
						.qty(dataLst[i].getQty())
						.quoteQty(dataLst[i].getQuoteQty())
						.time(triggerTime)
						.build();
				enaTradeHistoryInfoLst.add(enaTradeHistoryInfo);
			}
			
			enaTradeHistoryInfoLst = enaTradeHistoryInfoLst.stream().distinct().collect(Collectors.toList());
			enaTradeHistoryInfoRepository.saveAll(enaTradeHistoryInfoLst);
		} else
			if(currrency.equalsIgnoreCase("ETHFI")) {
				EthFITradeHistoryInfo   ethFITradeHistoryInfo = EthFITradeHistoryInfo.builder().build();
				List<EthFITradeHistoryInfo> ethFITradeHistoryInfoLst = new ArrayList<EthFITradeHistoryInfo>();
				long test_timestamp ;
				LocalDateTime triggerTime ;
				
				for(int i=0;i<dataLst.length;i++) {
					test_timestamp = Long.valueOf(dataLst[i].getTime());
					triggerTime =
					        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
					                                TimeZone.getDefault().toZoneId()); 
					ethFITradeHistoryInfo = EthFITradeHistoryInfo.builder()
							.id(dataLst[i].getId())
							.isBestMatch(dataLst[i].getIsBestMatch())
							.isBuyerMaker(dataLst[i].isBuyerMaker())
							.price(dataLst[i].getPrice())
							.qty(dataLst[i].getQty())
							.quoteQty(dataLst[i].getQuoteQty())
							.time(triggerTime)
							.build();
					ethFITradeHistoryInfoLst.add(ethFITradeHistoryInfo);
				}
				
				ethFITradeHistoryInfoLst = ethFITradeHistoryInfoLst.stream().distinct().collect(Collectors.toList());
				ethFITradeHistoryInfoRepository.saveAll(ethFITradeHistoryInfoLst);
		} 
	     else
			if(currrency.equalsIgnoreCase("W")) {
				WTradeInfo   wTradeInfo = WTradeInfo.builder().build();
				List<WTradeInfo> wTradeInfoLst = new ArrayList<WTradeInfo>();
				for(int i=0;i<dataLst.length;i++) {
					wTradeInfo = buildWTradeInfoEntity(dataLst[i]);
					wTradeInfoLst.add(wTradeInfo);
				}
				wTradeInfoRepository.saveAll(wTradeInfoLst);
		}else
			if(currrency.equalsIgnoreCase("BTC")) {
				BTCTradeHistoryInfo   btcTradeHistoryInfo = BTCTradeHistoryInfo.builder().build();
				List<BTCTradeHistoryInfo> btcTradeInfoLst = new ArrayList<BTCTradeHistoryInfo>();
				
				for(int i=0;i<dataLst.length;i++) {
					btcTradeHistoryInfo = buildBTCTradeHistoryInfoEntity(dataLst[i]);
					btcTradeInfoLst.add(btcTradeHistoryInfo);
				}
				btcTradeHistoryInfoRepository.saveAll(btcTradeInfoLst);
		} 
		return lastHistoricalId;
	}
  
  
  public static String getMaxIdTrade(List<TradeInfoDTO> tradeList) {
	  
	  String maxId = "-1";
	  Optional<TradeInfoDTO> maxTradeOpt =  tradeList.stream()
          .max(Comparator.comparingLong(trade -> Long.parseLong(trade.getId())));
	  if (maxTradeOpt.isPresent()) {
          System.out.println("Max trade id: " + maxTradeOpt.get().getId());
          maxId = maxTradeOpt.get().getId();
      }
	  return maxId;
  }
  
	
  
  
	public EnaTradeInfo buildEnaTradeInfoEntity(TradeInfoDTO data) {
		long test_timestamp = Long.valueOf(data.getTime());
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
		                                TimeZone.getDefault().toZoneId()); 
		
		EnaTradeInfo enaTradeInfo = EnaTradeInfo.builder().build();
		enaTradeInfo = EnaTradeInfo.builder()
				.id(data.getId())
				.isBestMatch(data.getIsBestMatch())
				.isBuyerMaker(data.isBuyerMaker())
				.price(data.getPrice())
				.qty(data.getQty())
				.quoteQty(data.getQuoteQty())
				.time(triggerTime)
				.build();
		return enaTradeInfo;
	}
	
	public EnaTradeInfo buildEthFITradeInfoEntity(TradeInfoDTO data) {
		long test_timestamp = Long.valueOf(data.getTime());
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
		                                TimeZone.getDefault().toZoneId()); 
		
		EnaTradeInfo enaTradeInfo = EnaTradeInfo.builder().build();
		enaTradeInfo = EnaTradeInfo.builder()
				.id(data.getId())
				.isBestMatch(data.getIsBestMatch())
				.isBuyerMaker(data.isBuyerMaker())
				.price(data.getPrice())
				.qty(data.getQty())
				.quoteQty(data.getQuoteQty())
				.time(triggerTime)
				.build();
		return enaTradeInfo;
	}
	
	public WTradeInfo buildWTradeInfoEntity(TradeInfoDTO data) {
		
		long test_timestamp = Long.valueOf(data.getTime());
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
		                                TimeZone.getDefault().toZoneId()); 
		
		WTradeInfo wTradeInfo = WTradeInfo.builder().build();
		wTradeInfo = WTradeInfo.builder()
				.id(data.getId())
				.isBestMatch(data.getIsBestMatch())
				.isBuyerMaker(data.isBuyerMaker())
				.price(data.getPrice())
				.qty(data.getQty())
				.quoteQty(data.getQuoteQty())
				.time(triggerTime)
				.build();
		return wTradeInfo;
	}
	
	
  public BTCTradeHistoryInfo buildBTCTradeHistoryInfoEntity(TradeInfoDTO data) {
	  
		long test_timestamp = Long.valueOf(data.getTime());
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
		                                TimeZone.getDefault().toZoneId()); 
		
		BTCTradeHistoryInfo btcTradeInfo = BTCTradeHistoryInfo.builder().build();
		btcTradeInfo = BTCTradeHistoryInfo.builder()
				.id(data.getId())
				.isBestMatch(data.getIsBestMatch())
				.isBuyerMaker(data.isBuyerMaker())
				.price(data.getPrice())
				.qty(data.getQty())
				.quoteQty(data.getQuoteQty())
				.time(triggerTime)
				.build();
		return btcTradeInfo;
	}
	
   public BTCTradeInfo buildBTCTradeInfoEntity(TradeInfoDTO data) {
		
		long test_timestamp = Long.valueOf(data.getTime());
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), 
		                                TimeZone.getDefault().toZoneId()); 
		
		BTCTradeInfo btcTradeInfo = BTCTradeInfo.builder().build();
		btcTradeInfo = BTCTradeInfo.builder()
				.id(data.getId())
				.isBestMatch(data.getIsBestMatch())
				.isBuyerMaker(data.isBuyerMaker())
				.price(data.getPrice())
				.qty(data.getQty())
				.quoteQty(data.getQuoteQty())
				.time(triggerTime)
				.build();
		return btcTradeInfo;
	}
	
	public GraphFulllResponseDTO getGraphData(@RequestBody GraphDataReqDTO req) {
		
		LocalDateTime fromDate = LocalDateTime.parse(req.getFromDate(), formatter);
		LocalDateTime toDate = LocalDateTime.parse(req.getToDate(), formatter);
		GraphGeneralResponseDTO respMax = null;
		GraphGeneralResponseDTO respMin = null;
		
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_data_for_graph",GraphResponseDTO.class);
   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
   		query.setParameter("cryptoCurrency",req.getCryptoCurrencyCode() );
   		query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("fromDate",fromDate );
   		query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("toDate",toDate );
   		query.registerStoredProcedureParameter("period", Integer.class, ParameterMode.IN);
   		query.setParameter("period",0 );
   		query.registerStoredProcedureParameter("currencytype", String.class, ParameterMode.IN);
   		// query.setParameter("currencytype",req.getDataType());
   		query.setParameter("currencytype",req.getDataType());
   		List<GraphResponseDTO> graphNormalResponseDTOlst = (List<GraphResponseDTO>) query.getResultList();
   		entityManager.clear();
		entityManager.close();
		GraphGeneralResponseDTO respNormal = GraphGeneralResponseDTO.builder()
				.data(graphNormalResponseDTOlst)
				.name("NORMAL")
				.build();
		
		
		/*
		query = this.entityManager.createStoredProcedureQuery("cr_data_for_graph",GraphResponseDTO.class);
   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
   		query.setParameter("cryptoCurrency",req.getCryptoCurrencyCode() );
   		query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("fromDate",fromDate );
   		query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("toDate",toDate );
   		query.registerStoredProcedureParameter("period", Integer.class, ParameterMode.IN);
   		query.setParameter("period",0 );
   		query.registerStoredProcedureParameter("currencytype", String.class, ParameterMode.IN);
   		// query.setParameter("currencytype",req.getDataType());
   		query.setParameter("currencytype","MAX");
   		List<GraphResponseDTO> graphMaxResponseDTOlst = (List<GraphResponseDTO>) query.getResultList();
   		entityManager.clear();
		entityManager.close();
		respMax = GraphGeneralResponseDTO.builder()
				.data(graphMaxResponseDTOlst)
				//.name(req.getDataType())
				.name("MAX")
				.build();
		
		
		
		
		query = this.entityManager.createStoredProcedureQuery("cr_data_for_graph",GraphResponseDTO.class);
   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
   		query.setParameter("cryptoCurrency",req.getCryptoCurrencyCode() );
   		query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("fromDate",fromDate );
   		query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
   		query.setParameter("toDate",toDate );
   		query.registerStoredProcedureParameter("period", Integer.class, ParameterMode.IN);
   		query.setParameter("period",0 );
   		query.registerStoredProcedureParameter("currencytype", String.class, ParameterMode.IN);
   		// query.setParameter("currencytype",req.getDataType());
   		query.setParameter("currencytype","MIN");
   		List<GraphResponseDTO> graphRespMinResponseDTOlst = (List<GraphResponseDTO>) query.getResultList();
   		entityManager.clear();
		entityManager.close();
		respMin = GraphGeneralResponseDTO.builder()
				.data(graphRespMinResponseDTOlst)
				//.name(req.getDataType())
				.name("MIN")
				.build();	
		*/
		
		GraphFulllResponseDTO resp = GraphFulllResponseDTO.builder()
				.dataMax(respMax)
				.dataMin(respMin)
				.dataNormal(respNormal)
				.build();
	
	   return resp; 
	}
	
	
     
      public GraphFulllResponseDTO getCandleGraphData(@RequestBody GraphDataReqDTO req) {

    	   GraphGeneralResponseDTO candleResponse = getCandleStickData(req);
    	   GraphGeneralResponseDTO volumeResponse = getVolumeData(req); 
    	    return GraphFulllResponseDTO.builder()
    	            .dataCandle(candleResponse)
    	            .dataVolume(volumeResponse)
    	            .build();
    	}

    public GraphGeneralResponseDTO getCandleStickData(GraphDataReqDTO req)
    {
        String tableName = CurrencyTableMapper.getTableName( req.getCryptoCurrencyCode());

        int pageSize = req.getSize();
 	    int pageNumber = req.getPage();

 	    // === First Query: Candle Data ===
 	    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("cr_dynamic_result", GraphResponseDTO.class);
 	    query.registerStoredProcedureParameter("fromDate", String.class, ParameterMode.IN);
 	    query.setParameter("fromDate", req.getFromDate());
 	    query.registerStoredProcedureParameter("toDate", String.class, ParameterMode.IN); // use consistent naming
 	    query.setParameter("toDate", req.getToDate());
 	    query.registerStoredProcedureParameter("tableName", String.class, ParameterMode.IN);
 	    query.setParameter("tableName", tableName);
 	    query.registerStoredProcedureParameter("criteria", String.class, ParameterMode.IN);
 	    query.setParameter("criteria", "candle");
 	    query.registerStoredProcedureParameter("period", String.class, ParameterMode.IN);
 	    query.setParameter("period", req.getPeriod());
 	    query.registerStoredProcedureParameter("pageSize", Integer.class, ParameterMode.IN);
 	    query.setParameter("pageSize", pageSize);
 	    query.registerStoredProcedureParameter("pageNumber", Integer.class, ParameterMode.IN);
 	    query.setParameter("pageNumber", pageNumber);
 	    query.registerStoredProcedureParameter("totalRecords", Integer.class, ParameterMode.OUT);

 	    query.execute();
 	    
 	    System.out.println("fromDate "+ req.getFromDate());
 	    System.out.println("toDate "+ req.getToDate());
 	    System.out.println("tableName "+ tableName);
	    System.out.println("criteria "+ "candle");
	    System.out.println("period "+ req.getPeriod());
 	    System.out.println("pageSize "+ pageSize);
 	    System.out.println("pageNumber "+ pageNumber);
 	    
 	    int totalRecords = (Integer) query.getOutputParameterValue("totalRecords");
 	    int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

 	    List<GraphResponseDTO> dataList = query.getResultList();
 	    entityManager.clear();
		entityManager.close();
 	    GraphGeneralResponseDTO response = GraphGeneralResponseDTO.builder()
 	            .data(dataList)
 	            .name("CANDLESTICKS")
 	            .totalRecords(totalRecords)
 	            .totalPages(totalPages)
 	            .build();
 	    return response;
 	    
 	   
    }
    
    public GraphGeneralResponseDTO getVolumeData(GraphDataReqDTO req)
    {
        String tableName = CurrencyTableMapper.getTableName( req.getCryptoCurrencyCode());
 	    int pageSize = req.getSize();
 	    int pageNumber = req.getPage();

 	    // === First Query: Candle Data ===
 	    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("cr_dynamic_result", GraphResponseDTO.class);
 	    query.registerStoredProcedureParameter("fromDate", String.class, ParameterMode.IN);
 	    query.setParameter("fromDate", req.getFromDate());
 	    query.registerStoredProcedureParameter("toDate", String.class, ParameterMode.IN); // use consistent naming
 	    query.setParameter("toDate", req.getToDate());
 	    query.registerStoredProcedureParameter("tableName", String.class, ParameterMode.IN);
 	    query.setParameter("tableName", tableName);
 	    query.registerStoredProcedureParameter("criteria", String.class, ParameterMode.IN);
 	    query.setParameter("criteria", "volume");
 	    query.registerStoredProcedureParameter("period", String.class, ParameterMode.IN);
 	    query.setParameter("period", req.getPeriod());
 	    query.registerStoredProcedureParameter("pageSize", Integer.class, ParameterMode.IN);
 	    query.setParameter("pageSize", pageSize);
 	    query.registerStoredProcedureParameter("pageNumber", Integer.class, ParameterMode.IN);
 	    query.setParameter("pageNumber", pageNumber);
 	    query.registerStoredProcedureParameter("totalRecords", Integer.class, ParameterMode.OUT);

 	    query.execute();
 	    int totalRecords = (Integer) query.getOutputParameterValue("totalRecords");
 	    int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

 	    List<GraphResponseDTO> dataList = query.getResultList();
 	    entityManager.clear();
		entityManager.close();
 	   GraphGeneralResponseDTO response = GraphGeneralResponseDTO.builder()
	            .data(dataList)
	            .name("VOLUME")
	            .totalRecords(totalRecords)
	            .totalPages(totalPages)
	            .build();
	   
 	    return response;
    }
	public SupportResistantPointsDTO getSupportResistantForGraph(@RequestBody GraphDataReqDTO req) {
		
		
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_support_resistant_for_graph",SupResDTO.class);
   		query.registerStoredProcedureParameter("cryptoCurrency", String.class, ParameterMode.IN);
   		query.setParameter("cryptoCurrency",req.getCryptoCurrencyCode() );
   		List<SupResDTO> supResDTOLst = (List<SupResDTO>) query.getResultList();
   		entityManager.clear();
		entityManager.close();
		SupportResistantPointsDTO suppResPts = SupportResistantPointsDTO.builder().build();
		Support sup = Support.builder().build();
		Resistant res = Resistant.builder().build();
		int i = 1;
		for(SupResDTO each : supResDTOLst) {
			if(i==1) {
				sup.setSupport1(each.getSupport());
				res.setResistant1(each.getResistant());
			}
			if(i==2) {
				sup.setSupport2(each.getSupport());
				res.setResistant2(each.getResistant());
			}
			if(i==3) {
				sup.setSupport3(each.getSupport());
				res.setResistant3(each.getResistant());
			}
			i++;
		}
		suppResPts.setSupport(sup);
		suppResPts.setResistant(res);
		return suppResPts;
	}
	
    public TradeResponseDTO getTradeHistory( TradeReqDTO req) {
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("cr_analyse_trade_infor_history_for_graph",TradeHistoryResDTO.class);
   		query.registerStoredProcedureParameter("currencyCode", String.class, ParameterMode.IN);
   		query.setParameter("currencyCode",req.getCurrencyCode() );
   		
   		query.registerStoredProcedureParameter("datePoint", String.class, ParameterMode.IN);
   		query.setParameter("datePoint",req.getDatePoint() );
   		
   		query.registerStoredProcedureParameter("intervals", String.class, ParameterMode.IN);
   		query.setParameter("intervals",req.getIntervals() );
   		
   		List<TradeHistoryResDTO> tradeHistoryResDTOLst = (List<TradeHistoryResDTO>) query.getResultList();
   		entityManager.clear();
		entityManager.close();
		TradeHistoryResDTO tradeHistoryResDTO = TradeHistoryResDTO.builder().build();
		TradeResponseDTO tradeResponseDTO = TradeResponseDTO.builder().build();
		List respArr= new ArrayList<>();
		
		if(tradeHistoryResDTOLst.size()>0) {
			tradeHistoryResDTO = tradeHistoryResDTOLst.get(0);
	    
		    respArr.add(tradeHistoryResDTO.getBuy15Min());
		    respArr.add(tradeHistoryResDTO.getSell15Min());
		    tradeResponseDTO.setHistory15Min(respArr);
		    
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy30Min());
		    respArr.add(tradeHistoryResDTO.getSell30Min());
		    tradeResponseDTO.setHistory30Min(respArr);
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy45Min());
		    respArr.add(tradeHistoryResDTO.getSell45Min());
		    tradeResponseDTO.setHistory45Min(respArr);
		    
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy1Hour());
		    respArr.add(tradeHistoryResDTO.getSell1Hour());
		    tradeResponseDTO.setHistory1Hour(respArr);
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy2Hour());
		    respArr.add(tradeHistoryResDTO.getSell2Hour());
		    tradeResponseDTO.setHistory2Hour(respArr);
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy4Hour());
		    respArr.add(tradeHistoryResDTO.getSell4Hour());
		    tradeResponseDTO.setHistory4Hour(respArr);
		    
		    respArr= new ArrayList<>();
		    respArr.add(tradeHistoryResDTO.getBuy1Day());
		    respArr.add(tradeHistoryResDTO.getSell1Day());
		    tradeResponseDTO.setHistory1Day(respArr);
		}
	    
		return tradeResponseDTO;
	}
    
    public List<CurrencyDTO> getCurrencyList(){
    	
    	List<CurrencyDTO> lst = new ArrayList<CurrencyDTO>();
    	CurrencyDTO dto = CurrencyDTO.builder().id(1)
    			.symbol("ENNA")
    			.name("ENNA")
    			.build();
    	lst.add(dto);
    	
    	dto = CurrencyDTO.builder().id(2)
    			.symbol("ETHFI")
    			.name("ETHFI")
    			.build();
    	lst.add(dto);
    	return lst;
    }
	
}

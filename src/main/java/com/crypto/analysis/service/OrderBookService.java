package com.crypto.analysis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.crypto.analysis.domain.CRBTCOrderBook;
import com.crypto.analysis.dto.GraphDataReqDTO;
import com.crypto.analysis.dto.GraphResponseProjection;
import com.crypto.analysis.dto.OrderBookByActionObjectProjection;
import com.crypto.analysis.dto.OrderBookResponseDTO;
import com.crypto.analysis.dto.orderBook.OrderBookBidAskListDTO;
import com.crypto.analysis.dto.orderBook.OrderBookDataDTO;
import com.crypto.analysis.dto.orderBook.OrderBookPercentageResponseDTO;
import com.crypto.analysis.dto.prices.BtcDTO;
import com.crypto.analysis.dto.prices.CurrencyPreviousPriceDTO;
import com.crypto.analysis.repositories.BtcRepository;
import com.crypto.analysis.repositories.CRBTCOrderBookConsolidatedRepository;
import com.crypto.analysis.repositories.CRBTCOrderBookRepository;

@Service
public class OrderBookService {

	 @Autowired
		CRBTCOrderBookRepository btcOrderBookRepository;
	 @Autowired
	 CRBTCOrderBookConsolidatedRepository  crBTCOrderBookConsolidatedRepository;

	 
	 @Autowired
	 PriceService  priceService;
	
	 
	 public void saveOrderBookLst(List<CRBTCOrderBook> binanceOrderBookLst) {
		 System.out.println("saving Orders");
		 btcOrderBookRepository.saveAll(binanceOrderBookLst);
	     
	 }
	 
     public List<GraphResponseProjection> getOrderBookPercentage(GraphDataReqDTO req) {
    	
    	if(req.getHmd().equalsIgnoreCase("HOUR"))
    		return crBTCOrderBookConsolidatedRepository.getOrderBookConsolidatedHourPeriod(req.getPeriod());
    	if(req.getHmd().equalsIgnoreCase("MINUTE"))
    		return crBTCOrderBookConsolidatedRepository.getOrderBookConsolidatedMinutePeriod(req.getPeriod());
    	
    	return new ArrayList<>();
    		
	 }
     
     public OrderBookResponseDTO getOrderBookByBidAsk(GraphDataReqDTO req) {
     	
    	 List<OrderBookByActionObjectProjection>  orderBookLstBuy = crBTCOrderBookConsolidatedRepository.getOrderBookByAction(req.getLimit(), "buy");
    	 List<OrderBookByActionObjectProjection>  orderBookLstSell = crBTCOrderBookConsolidatedRepository.getOrderBookByAction(req.getLimit(), "sell");
    	 
    	 OrderBookResponseDTO resp = OrderBookResponseDTO.builder()
    			 .ask(orderBookLstBuy)
    			 .bid(orderBookLstSell)
    			 .build();
    	 return resp;
     		
 	 }
     
     public OrderBookDataDTO getOrderBookData(GraphDataReqDTO req) {
      	
    	 
    	 
    	 if(req.getCryptoCurrencyCode().equalsIgnoreCase("btc"))
    	 {
	    	 List<OrderBookByActionObjectProjection>  orderBookLstBuy = crBTCOrderBookConsolidatedRepository.getOrderBookByAction(req.getLimit(), "buy");
	    	 List<OrderBookByActionObjectProjection>  orderBookLstSell = crBTCOrderBookConsolidatedRepository.getOrderBookByAction(req.getLimit(), "sell");
	    	
	    	 List<GraphResponseProjection> orderBookPercentage = getOrderBookPercentage(req) ;
	    	 
	    	 OrderBookPercentageResponseDTO orderBookPercent = OrderBookPercentageResponseDTO.builder()
	    			 .orderBookPercentage(orderBookPercentage)
	    			 .build();
	    	 
	    	 OrderBookBidAskListDTO orderBookData = OrderBookBidAskListDTO.builder().ask(orderBookLstBuy).bid(orderBookLstSell).build();
	    	 
	    	 List<BtcDTO> btcDTOLst = priceService.getCurrentPreviousPrice();
	    	 
	    	 CurrencyPreviousPriceDTO currencyPreviousPriceDTO = CurrencyPreviousPriceDTO.builder()
	    			 .currentPrice(btcDTOLst.get(0).getValue())
	    			 .previousPrice(btcDTOLst.get(1).getValue())
	    			 .build();
	    	 
	    	 OrderBookDataDTO orderBookDataDTO = OrderBookDataDTO.builder()
	    			 .currencyPreviousPriceDTO(currencyPreviousPriceDTO)
	    			 .orderBookDataBidAsk(orderBookData)
	    			 .orderBookPercentage(orderBookPercent)
	    			 .build();
	    	 
	    	 return orderBookDataDTO;
    	 }
    	 
    	 return null;
    	 
    	 
     		
 	 }
     
	 
	 
	 
	    
}

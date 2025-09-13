package com.crypto.analysis.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.crypto.analysis.domain.CrBTCHighLow;
import com.crypto.analysis.domain.CrBinanceHighLow;
import com.crypto.analysis.domain.CrEthereumHighLow;
import com.crypto.analysis.domain.CrShibaHighLow;
import com.crypto.analysis.domain.CrSolanaHighLow;
import com.crypto.analysis.domain.CrXrpHighLow;
import com.crypto.analysis.dto.CrCryptoDTO;
import com.crypto.analysis.dto.CurrencyDTO;
import com.crypto.analysis.dto.DataDTO;
import com.crypto.analysis.dto.GraphDataReqDTO;
import com.crypto.analysis.dto.GraphFulllResponseDTO;
import com.crypto.analysis.dto.GraphGeneralResponseDTO;
import com.crypto.analysis.dto.GraphResponseDTO;
import com.crypto.analysis.dto.GraphResponseProjection;
import com.crypto.analysis.dto.OrderBookResponseDTO;
import com.crypto.analysis.dto.SupportResistantPointsDTO;
import com.crypto.analysis.dto.TradeHistoryResDTO;
import com.crypto.analysis.dto.TradeReqDTO;
import com.crypto.analysis.dto.TradeResponseDTO;
import com.crypto.analysis.service.CryptoAnalyseHighLowService;
import com.crypto.analysis.service.CryptoAnalyseService;


@RestController
public class CryptoAnalyseController {

	@Autowired
	private final CryptoAnalyseService cryptoAnalyseService;
	
	@Autowired
	private final CryptoAnalyseHighLowService cryptoAnalyseHighLowService;

	public CryptoAnalyseController(CryptoAnalyseService cryptoAnalyseService , 
								   CryptoAnalyseHighLowService cryptoAnalyseHighLowService) {
		this.cryptoAnalyseService = cryptoAnalyseService;
		this.cryptoAnalyseHighLowService = cryptoAnalyseHighLowService;
	}

	@RequestMapping(value = "/input-screen")
	public ModelAndView dataReadRxcelWritedb(ModelMap model) {
		return new ModelAndView("html/index");
	}
	
	@RequestMapping(value = "/graph")
	public ModelAndView dataGraph(ModelMap model) {
		return new ModelAndView("html/graph");
	}
	@RequestMapping(value = "/dashboard")
	public ModelAndView dashboardView(ModelMap model) {
		return new ModelAndView("html/dashboard/index");
	}

	@GetMapping("/tablename")
	public ResponseEntity<List<Map<String, String>>> getTableNameEnum() {

		return ResponseEntity.ok(cryptoAnalyseService.getTableNameEnum());
	}

	/*
	@PostMapping("/insertData")
	public boolean insertData(@RequestBody DataDTO dataDTO) {
		return cryptoAnalyseService.insertIntoTable(dataDTO);
	}
	*/

	@GetMapping("/getdata/{tableName}")
	public ResponseEntity<List<DataDTO>> getData(@PathVariable("tableName") String tableName) {

		return ResponseEntity.ok(cryptoAnalyseService.getData(tableName));
	}

	@PostMapping("/updatedata")
	public boolean updateData(@RequestBody DataDTO dataDTO) {
		return cryptoAnalyseService.updateData(dataDTO);
	}
	@DeleteMapping(value = "deletedata/{tablename}/{id}")
	public ResponseEntity<HttpStatus> deleteData(@PathVariable("tablename") String tablename, @PathVariable("id") String id) {
		cryptoAnalyseService.deleteData(tablename,id);
		return new ResponseEntity<>(HttpStatus.OK);
	} 
	
	@PostMapping(value = "getGraphData")
	public GraphFulllResponseDTO getGraphData(@RequestBody GraphDataReqDTO req) {
		  req.setFromDate("2025-02-21 21:36:04");
		  req.setToDate("2025-02-21 21:36:04");
		 return cryptoAnalyseService.getGraphData(req);
	}
	
	// to get candle
	@PostMapping(value = "getCandleGraphData")
	public GraphFulllResponseDTO getCandleGraphData(@RequestBody GraphDataReqDTO req) {
		return cryptoAnalyseService.getCandleGraphData(req);
	}
	@PostMapping(value = "getSupportResistantForGraph")
	public ResponseEntity<SupportResistantPointsDTO> getSupportResistantForGraph(@RequestBody GraphDataReqDTO req) {
		
		SupportResistantPointsDTO resp = cryptoAnalyseService.getSupportResistantForGraph(req);
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	
	@PostMapping(value = "data/trade/history")
	public ResponseEntity<TradeResponseDTO> getTradeHistory(@RequestBody TradeReqDTO req) {
		
		TradeResponseDTO resp = cryptoAnalyseService.getTradeHistory(req);
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	
	@GetMapping(value = "data/currency/list")
	public ResponseEntity<List<CurrencyDTO>> getCurrencyList() {
		
		List<CurrencyDTO> resp = cryptoAnalyseService.getCurrencyList();
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}

	
	  @GetMapping(value = "/api/btc/fundingRate/{symbol}/{fromDate}/{toDate}") 
	  public ResponseEntity<List<GraphResponseDTO>> getfundingRateBtc(@PathVariable("symbol") String symbol, @PathVariable("fromDate") String fromDate, @PathVariable("toDate") String toDate) { 
		  List<GraphResponseDTO> results = cryptoAnalyseHighLowService.getfundingRate(symbol, fromDate, toDate); 
		  return ResponseEntity.ok(results); 
	  }
		/*
		 * @GetMapping(value = "/api/btc/latest") public ResponseEntity<CrBTCHighLow>
		 * getLatestBtc() { return
		 * cryptoAnalyseHighLowService.getLatestBtc().map(ResponseEntity::ok).orElseGet(
		 * () -> ResponseEntity.noContent().build()); }
		 */
	  
	@GetMapping(value = "/api/btc/latest")
	public ResponseEntity<List<CrCryptoDTO>> getLatestBtc() {
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestBtcList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
	@GetMapping(value = "/api/eth/latest")
	public ResponseEntity<List<CrCryptoDTO>> getLatestEthereum() {
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestEthList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
	@GetMapping(value = "/api/sol/latest")
	   public ResponseEntity<List<CrCryptoDTO>> getLatestSolana() {
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestSolList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
	

	@GetMapping(value = "/api/shib/latest")
	   public ResponseEntity<List<CrCryptoDTO>> getLatestShiba(){
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestShibaList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
	

	@GetMapping(value = "/api/bnb/latest")
	   public ResponseEntity<List<CrCryptoDTO>> getLatestBinance(){
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestBnbList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
	
	@GetMapping(value = "/api/xrp/latest")
	   public ResponseEntity<List<CrCryptoDTO>> getLatestXrp() {
	    List<CrCryptoDTO> results = cryptoAnalyseHighLowService.getLatestXrpList();
	    if (results.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(results);
	}
}

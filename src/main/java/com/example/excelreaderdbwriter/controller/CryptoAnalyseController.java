package com.example.excelreaderdbwriter.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.dto.DataDTO;
import com.example.excelreaderdbwriter.enums.TableNameEnum;
import com.example.excelreaderdbwriter.service.CryptoAnalyseService;
@RestController
public class CryptoAnalyseController {
	
	@Autowired
	private final CryptoAnalyseService cryptoAnalyseService;
	
	public CryptoAnalyseController(
			CryptoAnalyseService cryptoAnalyseService
			)
	{
		this.cryptoAnalyseService   = cryptoAnalyseService;
	}
	
	 
	@RequestMapping(value =  "/input-screen")
    public ModelAndView dataReadRxcelWritedb(ModelMap model)
    {
		return new ModelAndView("html/index");
    }

	@GetMapping("/tablename")
    public ResponseEntity<List<Map<String, String>>> getTableNameEnum() {
       
        return ResponseEntity.ok(cryptoAnalyseService.getTableNameEnum());
    }
	
	 @PostMapping("/insertData")
	   public void insertData(@RequestBody DataDTO dataDTO) {
		 cryptoAnalyseService.insertIntoTable(dataDTO);
	    }
}

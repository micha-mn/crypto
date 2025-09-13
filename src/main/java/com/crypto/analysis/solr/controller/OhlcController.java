package com.crypto.analysis.solr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.crypto.analysis.solr.dto.OhlcDoc;
import com.crypto.analysis.solr.dto.OhlcPoint;
import com.crypto.analysis.solr.service.OhlcQueryService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/ohlc")
@RequiredArgsConstructor
public class OhlcController {
  private final OhlcQueryService svc;

  // GET /api/ohlc/search?table=cr_btc_high_low&period=1h&from=2025-04-17T00:00:00Z&to=2025-04-18T00:00:00Z&page=0&size=100&asc=true
  @GetMapping("/search")
  public List<OhlcPoint> search(
      @RequestParam String table,
      @RequestParam String period,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "100") int size,
      @RequestParam(defaultValue = "true") boolean asc
  ) throws Exception {
    return svc.search(table, period, from, to, page, size, asc);
  }

  // GET /api/ohlc/{id}
  @GetMapping("/{id}")
  public OhlcDoc byId(@PathVariable String id) throws Exception {
    return svc.byId(id);
  }
}
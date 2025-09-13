package com.crypto.analysis.solr.service;

import org.apache.solr.common.SolrInputDocument;

import java.time.Instant;
import java.util.Map;

public interface ProcSource {
  String name();              // unique key for checkpointing (e.g., "btc_1h")
  String collection();        // Solr collection name
  ProcPage callPage(Instant from, Instant to, int size, int page) throws Exception;
  SolrInputDocument mapRow(Map<String, Object> row);
}
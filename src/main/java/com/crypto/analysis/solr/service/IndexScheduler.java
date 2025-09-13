package com.crypto.analysis.solr.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crypto.analysis.solr.domain.Checkpoint;
import com.crypto.analysis.solr.repositories.CheckpointRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexScheduler {

  private static final int BATCH = 2000;

  private final List<ProcSource> sources;
  private final SolrClient solr;
  private final CheckpointRepo checkpointRepo;  // ✅ JPA repo

  @Scheduled(fixedDelayString = "PT1M")
  public void runAll() {
	  System.out.println("▶ Starting scheduled indexing run...");
    for (ProcSource src : sources) {
      try {
        runOne(src);
      } catch (Exception e) {
    	  System.err.println("❌ Error running source {}"+ src.name() + e.getMessage());
      }
    }
    System.out.println("✔ Finished scheduled indexing run.");
  }

  private void runOne(ProcSource src) throws Exception {
    // ✅ Read last checkpoint, fallback to EPOCH
    Instant from = checkpointRepo.findById(src.name())
        .map(Checkpoint::getLastIndexedUtc)
        .orElse(Instant.EPOCH);

    Instant to = Instant.now();

    int page = 0;
    int total = Integer.MAX_VALUE;

    while (page * BATCH < total) {
      ProcPage p = src.callPage(from, to, BATCH, page);
      List<Map<String,Object>> rows = p.rows();
      if (rows.isEmpty()) break;

      List<SolrInputDocument> docs = new ArrayList<>(rows.size());
      for (Map<String,Object> r : rows) {
        docs.add(src.mapRow(r));
      }

      solr.add(src.collection(), docs, 10_000);
      page++;
      total = p.totalRecords();
    }

    solr.commit(src.collection());

    // ✅ Save or update checkpoint
    checkpointRepo.save(new Checkpoint(src.name(), to));
  }
}
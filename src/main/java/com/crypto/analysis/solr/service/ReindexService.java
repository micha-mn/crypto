package com.crypto.analysis.solr.service;


import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReindexService {
  private final List<ProcSource> sources;
  private final SolrClient solr;
  private final com.crypto.analysis.solr.repositories.CheckpointRepo checkpointRepo;

  public void fullReindex(ProcSource src) throws Exception {
    Instant from = Instant.EPOCH;        // or query true MIN() once
    Instant to   = Instant.now();
    int page = 0, pageSize = 2000;

    while (true) {
      ProcPage p = src.callPage(from, to, pageSize, page);
      List<Map<String,Object>> rows = p.rows();
      if (rows.isEmpty()) break;

      var docs = rows.stream().map(src::mapRow).toList();
      solr.add(src.collection(), docs, 10_000);
      if (rows.size() < pageSize) break; // last page
      page++;
    }
    solr.commit(src.collection());
    checkpointRepo.save(new com.crypto.analysis.solr.domain.Checkpoint(src.name(), to));
  }

  public void fullReindexAll() throws Exception {
    for (ProcSource src : sources) fullReindex(src);
  }
}
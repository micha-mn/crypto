package com.crypto.analysis.solr.controller;

import com.crypto.analysis.solr.service.ReindexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reindex")
public class ReindexController {
  private final ReindexService reindexService;

  @GetMapping("/all")
  public String reindexAll() throws Exception {
    reindexService.fullReindexAll();
    return "OK";
  }
}